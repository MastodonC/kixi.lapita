(ns schemas
  (:require [schema.core :as s]
            [schema.coerce :as coerce]
            [schema-contrib.core :as sc]))

;; Create and valid schemas using Plumatic schemas (https://github.com/plumatic/schema)
(defn make-ordered-ds-schema [col-vec]
  {:column-names (mapv #(s/one (s/eq (first %)) (str (first %))) col-vec)
   :columns (mapv #(s/one [(second %)] (format "col %s" (name (first %)))) col-vec)
   s/Keyword s/Any})

(defn make-row-schema
  [col-schema]
  (mapv (fn [s] (let [datatype (-> s :schema first)
                      fieldname (:name s)]
                  (s/one datatype fieldname)))
        (:columns col-schema)))

(defn make-col-names-schema
  [col-schema]
  (mapv (fn [s] (let [datatype (:schema s)
                      fieldname (:name s)]
                  (s/one datatype fieldname)))
        (:column-names col-schema)))

(defn apply-row-schema
  [col-schema csv-data]
  (let [row-schema (make-row-schema col-schema)]
    (map (coerce/coercer row-schema coerce/string-coercion-matcher)
         (:columns csv-data))))

(defn apply-col-names-schema
  [col-schema csv-data]
  (let [col-names-schema (make-col-names-schema col-schema)]
    ((coerce/coercer col-names-schema coerce/string-coercion-matcher)
     (:column-names csv-data))))

(defn apply-schema-coercion [data schema]
  {:column-names (apply-col-names-schema schema data)
   :columns (vec (apply-row-schema schema data))})

(def TestData1
  (make-ordered-ds-schema [[:col-1 s/Int] [:col-2 java.lang.Double]
                           [:col-3 s/Str] [:col-4 sc/Date]]))

(def TestData2
  (make-ordered-ds-schema [[:col-1 s/Int] [:col-2 s/Str] [:col-3 java.lang.Double]]))
