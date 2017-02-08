(ns kixi.lapita.schemas-test
  (:require [kixi.lapita.schemas :refer :all]
            [clojure.test :refer :all]
            [schema.core :as s]))

(def test-data-1 [{:col-1 "1" :col-2 "a" :col-3 "1.1"}
                  {:col-1 "2" :col-2 "b" :col-3 "bar"}
                  {:col-1 "foo" :col-2 "c" :col-3 "1.3"}])

(def test-data-2 [{:col-1 "1" :col-2 "a" :col-3 "1.1"}
                  {:col-1 "2" :col-2 "b" :col-3 "1.2"}
                  {:col-1 "3" :col-2 "c" :col-3 "1.3"}])

(def TestData
  {:col-1 s/Int
   :col-2 s/Str
   :col-3 java.lang.Double})

(deftest coerce-data-from-schema-test
  (testing "Data coercion is performed when no data inconsistencies"
    (is (= (coerce-data-from-schema test-data-2 TestData)
           [{:col-1 1 :col-2 "a" :col-3 1.1}
            {:col-1 2 :col-2 "b" :col-3 1.2}
            {:col-1 3 :col-2 "c" :col-3 1.3}])))
  (testing "Data is not performed when the data is inconsistent"
    (is (= (coerce-data-from-schema test-data-1 TestData)
           '({:col-1 1, :col-2 "a", :col-3 1.1}
             {:col-1 "2", :col-2 "b", :col-3 "bar"}
             {:col-1 "foo", :col-2 "c", :col-3 "1.3"}))))
  (testing "Data is marked with different metadata depending on its quality"
    (let [coerced-data-1 (coerce-data-from-schema test-data-1 TestData)
          coerced-data-2 (coerce-data-from-schema test-data-2 TestData)]
      (is (= (map meta coerced-data-1)
             '({:data-coercion :validated}
               {:data-coercion :errors}
               {:data-coercion :errors})))
      (is (= (map meta coerced-data-2)
             '({:data-coercion :validated}
               {:data-coercion :validated}
               {:data-coercion :validated}))))))

(deftest gather-errors-test
  (testing "Returns all non coercable data"
    (let [coerced-data-1 (coerce-data-from-schema test-data-1 TestData)
          coerced-data-2 (coerce-data-from-schema test-data-2 TestData)]
      (is (= (gather-errors coerced-data-1)
             '({:col-1 "2", :col-2 "b", :col-3 "bar"}
               {:col-1 "foo", :col-2 "c", :col-3 "1.3"})))
      (is (= (gather-errors coerced-data-2)
             '())))))

(deftest filter-out-errors-test
  (testing "Returns all coerced data"
    (let [coerced-data-1 (coerce-data-from-schema test-data-1 TestData)
          coerced-data-2 (coerce-data-from-schema test-data-2 TestData)]
      (is (= (filter-out-errors coerced-data-1)
             '({:col-1 1, :col-2 "a", :col-3 1.1})))
      (is (= (filter-out-errors coerced-data-2)
             '({:col-1 1 :col-2 "a" :col-3 1.1}
               {:col-1 2 :col-2 "b" :col-3 1.2}
               {:col-1 3 :col-2 "c" :col-3 1.3}))))))
