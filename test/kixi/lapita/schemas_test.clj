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

(deftest try-schema-coercion-test
  (testing "Data coercion is performed when no data inconsistencies"
    (is (= (try-schema-coercion test-data-2 TestData)
           {:coerced '({:col-1 1, :col-2 "a", :col-3 1.1}
                       {:col-1 2, :col-2 "b", :col-3 1.2}q
                       {:col-1 3, :col-2 "c", :col-3 1.3})})))
  (testing "Data is not performed when the data is inconsistent"
    (is (= (try-schema-coercion test-data-1 TestData)
           {:coerced '({:col-1 1, :col-2 "a", :col-3 1.1})
            :non-coerced '({:col-1 "2", :col-2 "b", :col-3 "bar"}
                           {:col-1 "foo", :col-2 "c", :col-3 "1.3"})}))))

(deftest gather-errors-test
  (testing "Returns all non coercable data"
    (let [coerced-data-1 (try-schema-coercion test-data-1 TestData)
          coerced-data-2 (try-schema-coercion test-data-2 TestData)]
      (is (= (gather-errors coerced-data-1)
             '({:col-1 "2", :col-2 "b", :col-3 "bar"}
               {:col-1 "foo", :col-2 "c", :col-3 "1.3"})))
      (is (= (gather-errors coerced-data-2)
             nil)))))

(deftest filter-out-errors-test
  (testing "Returns all coerced data"
    (let [coerced-data-1 (try-schema-coercion test-data-1 TestData)
          coerced-data-2 (try-schema-coercion test-data-2 TestData)]
      (is (= (filter-out-errors coerced-data-1)
             '({:col-1 1, :col-2 "a", :col-3 1.1})))
      (is (= (filter-out-errors coerced-data-2)
             '({:col-1 1 :col-2 "a" :col-3 1.1}
               {:col-1 2 :col-2 "b" :col-3 1.2}
               {:col-1 3 :col-2 "c" :col-3 1.3}))))))

(deftest gather-all-data-test
  (testing "Returns a mix of coerced and non-coerced data"
    (let [coerced-data-1 (try-schema-coercion test-data-1 TestData)
          coerced-data-2 (try-schema-coercion test-data-2 TestData)]
      (is (= (gather-all-data coerced-data-1)
             '({:col-1 "2", :col-2 "b", :col-3 "bar"}
               {:col-1 "foo", :col-2 "c", :col-3 "1.3"}
               {:col-1 1, :col-2 "a", :col-3 1.1})))
      (is (= (gather-all-data coerced-data-2)
             '({:col-1 1, :col-2 "a", :col-3 1.1}
               {:col-1 2, :col-2 "b", :col-3 1.2}
               {:col-1 3, :col-2 "c", :col-3 1.3}))))))
