(ns kixi.lapita
  (:gen-class)
  (:require [clojure.data.csv :as data-csv]
            [clojure.java.io :as io]
            [schema.coerce :as coerce]
            [clojure.core.matrix.dataset :as ds]
            [clojure.core.matrix :as mx]
            [witan.datasets :as wds]
            [schemas :as sc]))

;; Read and write files
(defn format-key
  "Handle keys that contains a space"
  [key]
  ((comp
    keyword
    clojure.string/lower-case
    #(clojure.string/replace % #" " "-"))
   key))

(defn load-csv
  "Loads csv file, returns a dataset."
  ([filename]
   (let [file (io/file filename)]
     (when (.exists (io/as-file file))
       (let [parsed-csv (with-open [in-file (io/reader file)]
                          (doall (data-csv/read-csv in-file)))
             parsed-data (rest parsed-csv)
             headers (map format-key (first parsed-csv))]
         (ds/dataset (map #(zipmap headers %) parsed-data))))))
  ([filename schema]
   (-> (let [file (io/file filename)]
         (when (.exists (io/as-file file))
           (let [parsed-csv (with-open [in-file (io/reader file)]
                              (doall (data-csv/read-csv in-file)))
                 parsed-data (rest parsed-csv)
                 headers (first parsed-csv)]
             {:column-names headers
              :columns (vec parsed-data)})))
       (sc/apply-schema-coercion schema)
       (as-> {:keys [column-names columns]} (ds/dataset column-names columns)))))

(defn write-csv
  "Write dataset to a csv file"
  [ds f]
  (let [rows-as-maps (ds/row-maps ds)
        colnames (mapv name (keys (first rows-as-maps)))
        rows (mapv #(vec (vals %)) rows-as-maps)]
    (with-open [out-file (io/writer f)]
      (data-csv/write-csv out-file (into [colnames] rows)))))

;; Get a preview of the data
(defn head
  "Look up the top n rows of the dataset.
   The default is to look up the first 5 rows."
  ([ds] (head ds 5))
  ([ds n]
   (wds/subset-ds ds :rows (range 0 n))))

(comment (head repairs-data 2))

(defn info [ds]
  {:column-names (:column-names ds)
   :num-rows (first (:shape ds))
   :num-columns (second (:shape ds))})

(comment (info repairs-data))

;; reducers from https://github.com/MastodonC/airsome/blob/master/src/airsome/core.clj
(defn maximum
  ([] ::-∞)
  ([x] (if (identical? ::-∞ x)
         ::-∞
         x))
  ([a b] (if (or (identical? ::-∞ a) (< a b)) b a)))

(defn minimum
  ([] ::+∞)
  ([x] (if (identical? ::+∞ x)
         ::+∞
         x))
  ([a b] (if (or (identical? ::+∞ a) (> a b)) b a)))
;;;;;;;;;;;;;;;;;

(defn describe [ds]
  (map (fn [col]
         (transduce (map col) maximum ds))
   :column-names))

;; More in depth
(defn count-elements-in-column
  "Takes in a dataset and a column name.
   Counts how many times an element appears in the column.
   Outputs the counts under the output column name."
  [ds input-col output-col]
  (->> (wds/group-ds ds input-col)
       (map (fn [[k v]] (merge k {output-col (mx/row-count v)})))
       (sort-by input-col)
       ds/dataset))


(comment (def repairs-data (load-csv "data/historic-repairs-2014-2015.csv"))

         (-> repairs-data
             (count-elements-in-column :property-reference :count-repairs-per-property)
             (write-csv "data/group-repairs-by-property.csv"))

         (-> repairs-data
             (count-elements-in-column :property-reference :count-repairs-per-property)
             (count-elements-in-column :count-repairs-per-property :occurences)
             (write-csv "data/summary-repairs-of-properties.csv")))
