(ns kixi.lapita.schemas
  (:require [schema.core :as s]
            [schema.coerce :as coerce]
            [schema-contrib.core :as c]
            [schema.utils :as su]))

;; Create and valid schemas using Plumatic schemas (https://github.com/plumatic/schema)

;; recently updated to return data even if inconsistent, but w/ extra metadata info
(defn coerce-data-from-schema
  "Performs the data coercion using a schema.
   If the data can be coerced, it's returned w/ :validated metadata associated.
   If there's a coercion error the non coerced data is returned associated w/ :errors
   metadata."
  [data schema]
  (let [data-coercer (coerce/coercer schema coerce/string-coercion-matcher)]
    (pmap (fn [d] (let [coerced (data-coercer d)]
                    (if (su/error? coerced)
                      (with-meta d {:data-coercion :errors})
                      (with-meta coerced {:data-coercion :validated}))))
          data)))

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

;; Looking for inconsistencies (data missing or wrong type) using schema coercion errors
(defn gather-errors
  "Returns the data records associated to :errors metadata."
  [data]
  (filter #(= (:data-coercion (meta %)) :errors) data))

(defn filter-out-errors
  "Returns the data records associated to :validated metadata."
  [data]
  (remove #(= (:data-coercion (meta %)) :errors) data))
