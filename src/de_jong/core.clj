(ns de-jong.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn make-attractor
  "Generates an de Jong attractor around the given parameters"
  [a b c d]
  (fn attractor
    [x y]
    [(- (q/sin (* a y)) (q/cos (* b x)))
     (- (q/sin (* c x)) (q/cos (* d y)))]))

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :hsb)
  (q/background 20)
  (q/stroke 122 255 255 20)
  (let [a -1.68661
        b -1.99168
        c 1.71743
        d -1.64958]
    {:attractor (make-attractor a b c d)
     :coords [0 0]}))

(defn update-state [{:keys [coords attractor] :as state}]
  (let [[x y] coords]
    (assoc state :coords (attractor x y))))

(defn draw-state [{:keys [coords]}]
  (let [x (q/map-range (first coords) -2 2 (* (q/width) 0.08) (* (q/width) 0.92))
        y (q/map-range (second coords) -2 2 (* (q/height) 0.08) (* (q/height) 0.92))]
    (q/point x y)))

(q/defsketch de-jong
  :title "De-Jong"
  :size [500 500]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/pause-on-error m/fun-mode])
