(ns kixi.lapita
  (:gen-class)
  (:require [clojure.data.csv :as data-csv]
            [clojure.java.io :as io]
            [schema.coerce :as coerce]
            [clojure.core.matrix.dataset :as ds]
            [clojure.core.matrix :as mx]
            [witan.datasets :as wds]
            [kixi.lapita.schemas :as sc]))

;;;;;;;;;;;;;;;;;;;;
;; kixi.lapita.io ;; - Read and write files
;;;;;;;;;;;;;;;;;;;;
(defn format-key
  "Handle column names keys that contain a space."
  [key]
  ((comp
    keyword
    clojure.string/lower-case
    #(clojure.string/replace % #" " "-"))
   key))

(defn load-csv
  "Loads csv file, returns a core.matrix dataset.
   If a schema is provided, the data is coerce to the right type."
  ([filename]
   (let [file (io/file filename)]
     (when (.exists (io/as-file file))
       (let [parsed-csv (with-open [in-file (io/reader file)]
                          (doall (data-csv/read-csv in-file)))
             parsed-data (rest parsed-csv)
             headers (map format-key (first parsed-csv))]
         (ds/dataset (map #(zipmap headers %) parsed-data))))))
  ([filename schema]
   (let [file (io/file filename)]
     (when (.exists (io/as-file file))
       (let [parsed-csv (with-open [in-file (io/reader file)]
                          (doall (data-csv/read-csv in-file)))
             parsed-data (rest parsed-csv)
             headers (map format-key (first parsed-csv))
             all-data (map #(zipmap headers %) parsed-data)
             coerced-data (sc/coerce-data-from-schema all-data schema)]
         (ds/dataset coerced-data))))))

(defn write-csv!
  "Write a dataset to a csv file"
  [ds f]
  (let [rows-as-maps (ds/row-maps ds)
        colnames (mapv name (keys (first rows-as-maps)))
        rows (mapv #(vec (vals %)) rows-as-maps)]
    (with-open [out-file (io/writer f)]
      (data-csv/write-csv out-file (into [colnames] rows)))))

;;;;;;;;;;;;;;;;;;;;;;;;;
;; kixi.lapita.preview ;; - Get a preview of the data
;;;;;;;;;;;;;;;;;;;;;;;;;
(defn info
  "Takes in a dataset, return information on the columns,
   number of rows and columns."
  [ds]
  {:column-names (:column-names ds)
   :num-rows (first (:shape ds))
   :num-columns (second (:shape ds))})

(defn head
  "Look up the top n rows of the dataset.
   The default is to look up the first 5 rows."
  ([ds] (if (>= (:num-rows (info ds)) 5)
          (head ds 5)
          ds))
  ([ds n]
   (wds/subset-ds ds :rows (range 0 n))))

;; reducers from a private MastodonC repo
;; https://github.com/MastodonC/airsome/blob/master/src/airsome/core.clj
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
;;;;;

;; Work in progress (transduce would work for Clojure data structure but not dataset)
(defn describe [ds]
  (map (fn [col]
         (transduce (map col) maximum ds))
       :column-names))

;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; kixi.lapita.transform ;;- Transform the data
;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn count-elements-in-column
  "Takes in a dataset and a column name.
   Counts how many times an element appears in the column.
   Outputs the counts under the output column name."
  [ds input-col output-col]
  (->> (wds/group-ds ds input-col)
       (map (fn [[k v]] (merge k {output-col (mx/row-count v)})))
       (sort-by input-col)
       ds/dataset))

;; Examples of uses
(comment (def repairs-data (load-csv "data/historic-repairs-2014-2015.csv"))

         (def repairs-data-coerced (load-csv "data/historic-repairs-2014-2015.csv"
                                             sc/HousingRepairData))

         (-> repairs-data
             (count-elements-in-column :property-reference :count-repairs-per-property)
             (write-csv "data/group-repairs-by-property.csv"))

         (-> repairs-data
             (count-elements-in-column :property-reference :count-repairs-per-property)
             (count-elements-in-column :count-repairs-per-property :occurences)
             (write-csv "data/summary-repairs-of-properties.csv")))
