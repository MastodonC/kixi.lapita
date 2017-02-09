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

(defn write-csv!
  "Write data (dataset or collection of maps) to a csv file"
  [data f]
  (let [rows-as-maps (if (ds/dataset? data) (ds/row-maps data) data)
        colnames (mapv name (keys (first rows-as-maps)))
        rows (mapv #(vec (vals %)) rows-as-maps)]
    (with-open [out-file (io/writer f)]
      (data-csv/write-csv out-file (into [colnames] rows)))))

(defn csv-to-maps-coll
  "Takes in a filename, return a map containing the column header
   and the column content in a vector of vectors."
  [filename]
  (let [file (io/file filename)]
    (when (.exists (io/as-file file))
      (let [parsed-csv (with-open [in-file (io/reader file)]
                         (doall (data-csv/read-csv in-file)))
            parsed-data (rest parsed-csv)
            headers (map format-key (first parsed-csv))]
        (map #(zipmap headers %) parsed-data)))))

(defn csv-to-dataset
  "Loads csv file, returns a core.matrix dataset.
   If a schema is provided alone, the data is coerced to the right type
   unless it leads to a coercion error.
   If a schema is provided with a map of options, then you can return only
   data consistent with the schema and either print or save to file the data
   that fails data coercion.
   Example of options {:print-errors true :write-errors 'data/errors.csv' :remove-errors true}"
  ([filename]
   (ds/dataset (csv-to-maps-coll filename)))
  ;; Returns a mix of coerced data and data inconsistent w/ schema
  ([filename schema]
   (csv-to-dataset filename schema {}))
  ;; Specify options in the 3rd arg to print number of rows with inconsistent data,
  ;; write inconsistent data to file return only coerced data.
  ([filename schema {:keys [print-errors write-errors remove-errors]}]
   (let [all-data (csv-to-maps-coll filename)
         data-after-coercion (sc/try-schema-coercion all-data schema)
         all-data-after-coercion (sc/gather-all-data data-after-coercion)
         errors (when (or print-errors write-errors) (sc/gather-errors data-after-coercion))]

     (when print-errors (println (format "There are %d rows with data coercion errors out of %d rows"
                                         (count errors) (count all-data-after-coercion))))
     (when write-errors
       (write-csv! errors write-errors)
       (println (format "The records with data coercion issues were saved in %s" write-errors)))
     (if remove-errors
       (ds/dataset (sc/filter-out-errors data-after-coercion))
       (ds/dataset all-data-after-coercion)))))

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
