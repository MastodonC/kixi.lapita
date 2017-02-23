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

(defn bar-chart
  ([ds x-axis y-axis]
   (bar-chart ds x-axis y-axis {}))
  ([ds x-axis y-axis {:keys [plot-color plot-width plot-height]}]
   (let [x-data (wds/subset-ds ds :cols x-axis)
         y-data (wds/subset-ds ds :cols y-axis)
         all-data (transform-data-for-plot ds x-data y-data)
         bar-width (calc-width-of-bar x-data)
         color-plot (or plot-color "#3D325A")
         width-plot (or plot-width 600)
         height-plot (or plot-height 320)]
     {:plot (-> (viz-spec x-data y-data width-plot height-plot)
                (assoc :data [((bar-spec all-data 2 bar-width) 0 color-plot)])
                (viz/svg-plot2d-cartesian))
      :width width-plot
      :height height-plot})))

(defn plot-bar-chart
  ([ds x-column y-column filepath]
   (plot-bar-chart ds x-column y-column filepath {}))
  ([ds x-column y-column filepath map-options]
   (let [{:keys [plot width height]} (bar-chart ds x-column y-column map-options)]
     (export-viz plot filepath width height))))
