(ns kixi.lapita.plot
  (:require [thi.ng.geom.viz.core :as viz]
            [thi.ng.geom.svg.core :as svg]
            [clojure.core.matrix.dataset :as ds]
            [witan.datasets :as wds]))

;; Implement example from thi.ng/geom
;; https://github.com/thi-ng/geom/blob/master/geom-viz/src/core.org#bar-graph

(defn export-viz
  [viz path plot-width plot-height]
  (->> viz
       (svg/svg {:width plot-width :height plot-height})
       (svg/serialize)
       (spit path)))

(defn transform-data-for-plot
  [ds x-data y-data]
  (map vector x-data y-data))

(defn calc-width-of-bar
  [x-data]
  (cond
    (<= (count x-data) 5) 70
    (and (> (count x-data) 5) (<= (count x-data) 20)) 20
    :else 10))

(defn bar-spec
  [data num width]
  (fn [idx col]
    {:values     data
     :attribs    {:stroke       col
                  :stroke-width (str (dec width) "px")}
     :layout     viz/svg-bar-plot
     :interleave num
     :bar-width  width
     :offset     idx}))

(defn viz-spec
  [x-data y-data plot-width plot-height]
  {:x-axis (viz/linear-axis
            {:domain [(let [min-x (reduce min x-data)]
                        (if (= min-x 0) 0 (dec min-x)))
                      (inc (reduce max x-data))]
             :range  [50 (- plot-width 20)]
             :major  1
             :pos    (- plot-height 40)
             :label  (viz/default-svg-label int)})
   :y-axis (viz/linear-axis
            {:domain      [0 (+ 5 (reduce max y-data))]
             :range       [(- plot-height 40) 20]
             :major       10
             :minor       5
             :pos         50
             :label-dist  15
             :label-style {:text-anchor "end"}})
   :grid   {:minor-y true}})

(defn plot-bar-chart
  ([ds x-axis y-axis output-path]
   (plot-bar-chart ds x-axis y-axis output-path {:plot-color "#3D325A"
                                                 :plot-width 600
                                                 :plot-height 320}))
  ([ds x-axis y-axis output-path {:keys [plot-color plot-width plot-height]}]
   (let [x-data (wds/subset-ds ds :cols x-axis)
         y-data (wds/subset-ds ds :cols y-axis)
         all-data (transform-data-for-plot ds x-data y-data)
         bar-width (calc-width-of-bar x-data)
         color-plot (or plot-color "#3D325A")
         width-plot (or plot-width 600)
         height-plot (or plot-height 320)]
     (-> (viz-spec x-data y-data width-plot height-plot)
         (assoc :data [((bar-spec all-data 2 bar-width) 0 color-plot)])
         (viz/svg-plot2d-cartesian)
         (export-viz output-path width-plot height-plot)))))

(comment
  (def test-data
    (ds/dataset [{:col-1 2000 :col-2 43}
                 {:col-1 2001 :col-2 47}
                 {:col-1 2002 :col-2 62}
                 {:col-1 2003 :col-2 43}
                 {:col-1 2004 :col-2 54}
                 {:col-1 2005 :col-2 57}
                 {:col-1 2006 :col-2 48}
                 {:col-1 2007 :col-2 58}
                 {:col-1 2008 :col-2 65}
                 {:col-1 2009 :col-2 68}
                 {:col-1 2010 :col-2 71}
                 {:col-1 2011 :col-2 74}
                 {:col-1 2012 :col-2 78}
                 {:col-1 2013 :col-2 63}
                 {:col-1 2014 :col-2 69}
                 {:col-1 2015 :col-2 80}
                 {:col-1 2016 :col-2 96}
                 {:col-1 2017 :col-2 85}
                 {:col-1 2018 :col-2 78}
                 {:col-1 2019 :col-2 96}]))

  (plot-bar-chart test-data :col-1 :col-2 "data/test-chart.svg"
                  {:plot-width 900 :plot-height 500}))
