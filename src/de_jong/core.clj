(ns de-jong.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def steps-per-frame 100)

(defn make-attractor
  "Generates an de Jong attractor around the given parameters"
  [a b c d]
  (fn attractor
    [[x y]]
    [(- (q/sin (* a y)) (q/cos (* b x)))
     (- (q/sin (* c x)) (q/cos (* d y)))]))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  (q/background 20)
  (q/fill 20)
  (let [a -1.68661
        b -1.99168
        c 1.71743
        d -1.64958]
    {:attractor (iterate (make-attractor a b c d) [0 0])
     :histogram (vec (repeat (* (q/width) (q/height)) 0))})) ; amount of times each pixel has been 'visited'

(defn ->screen-space
  "Takes a coordinate in de Jong space [-2, 2]x[-2, 2] and maps them to a range
  from l to h."
  [coord l h]
  (map #(Math/round (q/map-range % -2 2 l h)) coord))

(defn update-state [{:keys [attractor] :as state}]
  (let [coords (take steps-per-frame attractor)
        ;; we always take the smaller edge of w/h to ensure a nice circle
        m (min (q/width) (q/height))
        l (* 0.12 m)
        h (* 0.88 m)]
    {:attractor (drop steps-per-frame attractor)
     :histogram (loop [histogram (:histogram state)
                       coords (map #(->screen-space % l h) coords)]
                  (if (empty? coords)
                    histogram
                    (let [[x y] (first coords)]
                      (recur
                       (update histogram (+ (* y (q/width)) x) inc)
                       (rest coords)))))}))

(defn draw-state [{:keys [histogram]}]
  ;; clear drawing
  (q/no-stroke)
  (q/rect 0 0 (q/width) (q/height))
  ;; draw all dots with alpha values from 0 to 255
  (let [minv (apply min histogram)
        maxv (apply max histogram)
        w (q/width)]
    (doseq [[i v] (map-indexed vector histogram)]
      (let [x (mod i w)
            y (/ (- i x) w)]
        (q/stroke 122 255 255 (q/map-range v minv maxv 0 120))
        (q/point x y)))))

(q/defsketch de-jong
  :title "De-Jong"
  :size [500 500]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/pause-on-error m/fun-mode])
