(ns kixi.lapita-test
  (:require [clojure.test :refer :all]
            [kixi.lapita :refer :all]
            [clojure.core.matrix.dataset :as ds]))

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
