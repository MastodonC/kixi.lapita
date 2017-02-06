(ns kixi.lapita.schemas
  (:require [schema.core :as s]
            [schema.coerce :as coerce]
            [schema-contrib.core :as c]))

;; Create and valid schemas using Plumatic schemas (https://github.com/plumatic/schema)
(defn coerce-data-from-schema
  [data schema]
  (let [data-coercer (coerce/coercer schema coerce/string-coercion-matcher)]
    (pmap data-coercer data)))

;; NOTE: schema-contrib.core/Date expect a date in format ISO 8601 like YYYY-MM-DD
;; To solve that, change the format on the spreadsheet to match ISO 8601.
(def HousingRepairSchema
  {:repair-number s/Str
   :property-reference s/Str
   :original-target-date c/Date
   :target-end-date c/Date
   :logged-date c/Date
   :termination-date c/Date
   :priority-code s/Int
   :workforce-name s/Str
   :description-for-code s/Str})

;; !!! I changed the way we coerce data for core.matrix datasets !!!
;; When trying on a different dataset there might be an exception due
;; to the order of the keys in the schema map vs the actual map of data
