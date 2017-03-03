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

(defn calc-width-of-bar
  [x-data]
  (cond
    (<= (count x-data) 5) 70
    (and (> (count x-data) 5) (<= (count x-data) 20)) 20
    :else 10))

(defn shape [flip-point flip-point-height]
  (fn [[ax ay :as a] [bx by :as b] [domain-x domain-y]]
    (if (> flip-point domain-y)
      (let [diff (- ay flip-point-height)
            new-a [ax flip-point-height]
            new-b [bx (+ flip-point-height diff)]]
        (svg/line new-a new-b))
      (svg/line a [bx flip-point-height]))))

(defn bar-spec
  [data num width flip-point flip-point-height]
  (fn [idx col]
    {:values     data
     :attribs    {:stroke       col
                  :stroke-width (str (dec width) "px")}
     :shape (shape flip-point flip-point-height)
     :layout     viz/svg-bar-plot
     :interleave num
     :bar-width  width
     :offset     idx}))

(defn calc-y-range [plot-height]
  [(- plot-height 40) 20])

(defn calc-y-domain [y-data]
  (let [lower-y (let [min-y (reduce min y-data)]
                  (if (neg? min-y) (+ min-y (* 0.2 min-y)) 0))
        upper-y (let [max-y (reduce max y-data)] (+ (* 0.2 max-y) max-y))]
    [lower-y upper-y]))

(defn viz-spec
  [x-data y-data plot-width plot-height]
  (let [lower-x (let [min-x (reduce min x-data)]
                  (if (zero? min-x) 0 (dec min-x)))
        upper-x (inc (reduce max x-data))]
    {:x-axis (viz/linear-axis
              {:domain [lower-x upper-x]
               :range  [50 (- plot-width 20)]
               :major  1
               :pos    (- plot-height 40) ;;lower-y
               :label  (viz/default-svg-label int)})
     :y-axis (viz/linear-axis
              {:domain      (calc-y-domain y-data)
               :range       (calc-y-range plot-height)
               :major       10
               :minor       5
               :pos         50
               :label-dist  15
               :label-style {:text-anchor "end"}})
     :grid   {:minor-y true}}))

(defn calc-flip-point
  [flip-point plot-height y-data]
  (let [[top-y bottom-y] (calc-y-range plot-height)
        [lower-y upper-y] (calc-y-domain y-data)
        fp-ratio (- 1 (/ (- flip-point lower-y) (- upper-y lower-y)))
        range-ratio-applied (+ (* (- top-y bottom-y) fp-ratio) bottom-y)]
    range-ratio-applied))

(defn bar-chart
  ([ds x-axis y-axis]
   (bar-chart ds x-axis y-axis {}))
  ([ds x-axis y-axis {:keys [plot-color plot-width plot-height]}]
   (let [x-data (sort (wds/subset-ds ds :cols x-axis))
         x-min (reduce min x-data)
         x-max (reduce max x-data)
         y-data (sort (wds/subset-ds ds :cols y-axis))
         all-data (map vector x-data y-data)
         bar-width (calc-width-of-bar x-data)
         color-plot (or plot-color "#3D325A")
         width-plot (or plot-width 600)
         height-plot (or plot-height 320)
         flip-point 0 ;; All bars start at 0 (even when y values negative)
         flip-point-height (calc-flip-point flip-point height-plot y-data)
         flip-line {:values [[(dec x-min) 0] [(inc x-max) 0]]
                    :attribs {:fill "none" :stroke "#4c4a4a"}
                    :layout viz/svg-line-plot}
         plot ((bar-spec all-data 1 ;; Plot 1 data series for now
                         bar-width flip-point
                         flip-point-height) 0 ;; Index of data series is 0
                                            color-plot)
         ;; If y values negative, add line at y = 0
         plot-data (if (neg? (reduce min y-data)) [plot flip-line] [plot])]
     {:plot (-> (viz-spec x-data y-data width-plot height-plot)
                (assoc :data plot-data)
                (viz/svg-plot2d-cartesian))
      :width width-plot
      :height height-plot})))

(defn plot-bar-chart
  ([ds x-column y-column filepath]
   (plot-bar-chart ds x-column y-column filepath {}))
  ([ds x-column y-column filepath map-options]
   (let [{:keys [plot width height]} (bar-chart ds x-column y-column map-options)]
     (export-viz plot filepath width height))))
