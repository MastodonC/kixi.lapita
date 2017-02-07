(ns kixi.lapita-test
  (:require [clojure.test :refer :all]
            [kixi.lapita :refer :all]
            [clojure.core.matrix.dataset :as ds]
            [me.raynes.fs :as fs]
            [schema.core :as s]
            [schema-contrib.core :as c]))

(def test-dataset (ds/dataset [{:col-1 1 :col-2 "a" :col-3 1.1}
                               {:col-1 2 :col-2 "b" :col-3 1.2}
                               {:col-1 3 :col-2 "c" :col-3 1.3}]))

(def test-dataset2 (ds/dataset [{:col-1 1 :col-2 "a" :col-3 1.1}
                                {:col-1 2 :col-2 "b" :col-3 1.2}
                                {:col-1 3 :col-2 "c" :col-3 1.3}
                                {:col-1 4 :col-2 "d" :col-3 1.4}
                                {:col-1 5 :col-2 "e" :col-3 1.5}
                                {:col-1 6 :col-2 "f" :col-3 1.6}]))

(def test-dataset3 (ds/dataset [{:col-1 1 :col-2 "a" :col-3 1.1}
                                {:col-1 2 :col-2 "b" :col-3 1.2}
                                {:col-1 3 :col-2 "c" :col-3 1.3}
                                {:col-1 4 :col-2 "a" :col-3 1.4}
                                {:col-1 5 :col-2 "a" :col-3 1.5}
                                {:col-1 6 :col-2 "b" :col-3 1.6}]))

(def TestData1
  {:col-1 s/Int
   :col-2 java.lang.Double
   :col-3 s/Str
   :col-4 c/Date})

(def TestData2
  {:col-1 s/Int
   :col-2 s/Str
   :col-3 java.lang.Double})

(deftest csv-to-maps-coll-test
  (testing "CSV file loaded into a core Clojure data structure"
    (is (= (csv-to-maps-coll "test-data/test-data-1.csv")
           '({:col-1 "1" :col-2 "1.1" :col-3 "foo" :col-4 "2017-02-01"}
             {:col-1 "2" :col-2 "1.2" :col-3 "bar" :col-4 "2017-02-02"}
             {:col-1 "3" :col-2 "1.3" :col-3 "baz" :col-4 "2017-02-03"}
             {:col-1 "4" :col-2 "1.4" :col-3 "fizz" :col-4 "2017-02-04"}
             {:col-1 "5" :col-2 "1.5" :col-3 "buzz" :col-4 "2017-02-05"}
             {:col-1 "6" :col-2 "1.6" :col-3 "boo" :col-4 "2017-02-06"}
             {:col-1 "7" :col-2 "1.7" :col-3 "wiz" :col-4 "2017-02-07"})))))

(deftest csv-to-dataset-test
  (testing "CSV file loaded into a dataset - w/o a schema all values are considered strings"
    (is (= (csv-to-dataset "test-data/test-data-1.csv")
           (ds/dataset [{:col-1 "1" :col-2 "1.1" :col-3 "foo" :col-4 "2017-02-01"}
                        {:col-1 "2" :col-2 "1.2" :col-3 "bar" :col-4 "2017-02-02"}
                        {:col-1 "3" :col-2 "1.3" :col-3 "baz" :col-4 "2017-02-03"}
                        {:col-1 "4" :col-2 "1.4" :col-3 "fizz" :col-4 "2017-02-04"}
                        {:col-1 "5" :col-2 "1.5" :col-3 "buzz" :col-4 "2017-02-05"}
                        {:col-1 "6" :col-2 "1.6" :col-3 "boo" :col-4 "2017-02-06"}
                        {:col-1 "7" :col-2 "1.7" :col-3 "wiz" :col-4 "2017-02-07"}])))
    (testing "CSV file loaded into a dataset - w/ a schema data get coerced if no error."
      (is (= (csv-to-dataset "test-data/test-data-1.csv" TestData1)
             (ds/dataset [{:col-1 1 :col-2 1.1 :col-3 "foo" :col-4 "2017-02-01"}
                          {:col-1 2 :col-2 1.2 :col-3 "bar" :col-4 "2017-02-02"}
                          {:col-1 3 :col-2 1.3 :col-3 "baz" :col-4 "2017-02-03"}
                          {:col-1 4 :col-2 1.4 :col-3 "fizz" :col-4 "2017-02-04"}
                          {:col-1 5 :col-2 1.5 :col-3 "buzz" :col-4 "2017-02-05"}
                          {:col-1 6 :col-2 1.6 :col-3 "boo" :col-4 "2017-02-06"}
                          {:col-1 7 :col-2 1.7 :col-3 "wiz" :col-4 "2017-02-07"}]))))
    (testing "CSV file loaded into a dataset - w/ a schema, return coerced data and errors"
      ;; (is (= (csv-to-dataset "test-data/test-data-2.csv" TestData1)
      ;;        (ds/dataset [{:col-1 1 :col-2 1.1 :col-3 "foo" :col-4 "2017-02-01"}
      ;;                     {:col-1 2 :col-2 1.2 :col-3 "bar" :col-4 "2017-02-02"}
      ;;                     {:col-1 3 :col-2 1.3 :col-3 "baz" :col-4 "2017-02-03"}
      ;;                     {:col-1 4 :col-2 1.4 :col-3 "fizz" :col-4 "2017-02-04"}
      ;;                     {:col-1 5 :col-2 1.5 :col-3 "buzz" :col-4 "2017-02-05"}
      ;;                     {:col-1 6 :col-2 1.6 :col-3 "boo" :col-4 "FOO"}
      ;;                     {:col-1 "bar" :col-2 1.7 :col-3 "" :col-4 "2017-02-07"}])))
      )
    (testing "CSV file loaded into a dataset - w/ a schema, return only coerced data"
      (is (= (csv-to-dataset "test-data/test-data-2.csv" TestData1
                             {:print-errors false :write-errors false :remove-errors true})
             (ds/dataset [{:col-1 1 :col-2 1.1 :col-3 "foo" :col-4 "2017-02-01"}
                          {:col-1 2 :col-2 1.2 :col-3 "bar" :col-4 "2017-02-02"}
                          {:col-1 3 :col-2 1.3 :col-3 "baz" :col-4 "2017-02-03"}
                          {:col-1 4 :col-2 1.4 :col-3 "fizz" :col-4 "2017-02-04"}
                          {:col-1 5 :col-2 1.5 :col-3 "buzz" :col-4 "2017-02-05"}]))))))

(deftest write-csv-test
  (testing "A CSV is created when calling the write-csv function"
    (let [tmp (fs/temp-file "test-write-data-")]
      (write-csv! test-dataset2 tmp)
      (is (= test-dataset2
             (csv-to-dataset tmp TestData2))))))

(deftest head-test
  (testing "Returns the number of rows wanted"
    (is (= (head test-dataset 2)
           (ds/dataset [{:col-1 1 :col-2 "a" :col-3 1.1}
                        {:col-1 2 :col-2 "b" :col-3 1.2}]))))
  (testing "Returns five rows by default"
    (is (= (head test-dataset2)
           (ds/dataset [{:col-1 1 :col-2 "a" :col-3 1.1}
                        {:col-1 2 :col-2 "b" :col-3 1.2}
                        {:col-1 3 :col-2 "c" :col-3 1.3}
                        {:col-1 4 :col-2 "d" :col-3 1.4}
                        {:col-1 5 :col-2 "e" :col-3 1.5}]))))
  (testing "When less than 5 rows return dataset as is"
    (is (= (head test-dataset)
           (ds/dataset [{:col-1 1 :col-2 "a" :col-3 1.1}
                        {:col-1 2 :col-2 "b" :col-3 1.2}
                        {:col-1 3 :col-2 "c" :col-3 1.3}])))))

(deftest info-test
  (testing "Info about a dataset returned as expected"
    (is (= (info test-dataset)
           {:column-names [:col-1 :col-2 :col-3]
            :num-rows 3
            :num-columns 3}))))

(deftest count-elements-in-column-test
  (testing "A new dataset with elements count is returned"
    (is (= (count-elements-in-column test-dataset :col-2 :count-of-col-2)
           (ds/dataset [{:col-2 "a" :count-of-col-2 1}
                        {:col-2 "b" :count-of-col-2 1}
                        {:col-2 "c" :count-of-col-2 1}])))
    (is (= (count-elements-in-column test-dataset3 :col-2 :count-of-col-2)
           (ds/dataset [{:col-2 "a" :count-of-col-2 3}
                        {:col-2 "b" :count-of-col-2 2}
                        {:col-2 "c" :count-of-col-2 1}])))))
