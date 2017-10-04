(ns de-jong.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn step
  "Does the de-jong calculation of [x_n+1, y_n+1]"
  [a b c d x y]
  [(- (q/sin (* a y)) (q/cos (* b x)))
   (- (q/sin (* c x)) (q/cos (* d y)))])

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :hsb)
  (q/background 20)
  (q/stroke 122 255 255 20)
  (let [a -1.68661
        b -1.99168
        c 1.71743
        d -1.64958]
    {:params [a b c d]
     :coords [0 0]}))

(defn update-state [state]
  (let [[a b c d] (:params state)
        [x y] (:coords state)]
    (assoc state :coords (step a b c d x y))))

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
