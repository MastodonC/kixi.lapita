(ns kixi.lapita.schemas
  (:require [schema.core :as s]
            [schema.coerce :as coerce]
            [schema-contrib.core :as c]
            [schema.utils :as su]))

;; Create and valid schemas using Plumatic schemas (https://github.com/plumatic/schema)

;; Note: When the data cannot be coerced using schema, we still want to return it.
(defn try-schema-coercion
  "Takes in data as a coll of maps and a schema.
   Returns a map which separates coerced and non-coerced data."
  [data schema]
  (let [data-coercer (coerce/coercer schema coerce/string-coercion-matcher)]
    (->> data
         (map (fn [d] (let [coerced (data-coercer d)]
                        (if (su/error? coerced)
                          {:non-coerced (list d)}
                          {:coerced (list coerced)}))))
         (apply merge-with concat))))

;; !!! I changed the way we coerce data for core.matrix datasets !!!
;; When trying on a different dataset there might be an exception due
;; to the order of the keys in the schema map vs the actual map of data

;; Looking for inconsistencies (data missing or wrong type) using schema coercion errors
(defn gather-errors
  "Returns the non-coerced data records."
  [data]
  (:non-coerced data))

(defn filter-out-errors
  "Returns the coerced data records."
  [data]
  (:coerced data))

(defn gather-all-data
  [data]
  (concat (gather-errors data) (filter-out-errors data)))
