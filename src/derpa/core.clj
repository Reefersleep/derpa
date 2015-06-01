(ns derpa.core
  (:gen-class))

(defn initialize-cells [number] (vec (repeat number 0)))

(defn out-of-upper-sourcecode-bounds? [app-state]
  (= (:reader-position app-state) (count (:src app-state)))) ;;TODO currently only checks for upper bounds

(defn retrieve-current-symbol [app-state]
  (nth (:src app-state) (:reader-position app-state)))

(defn looping-forward? [app-state]
  (= :looping-forward (:movement app-state)))

(defn brackets-balanced? [app-state]
  (= (:nested-left-brackets app-state) (:nested-right-brackets app-state)))

(defn looping-backward? [app-state]
  (= :looping-backward (:movement app-state)))

(defn step [app-state]
  (let [{:keys [src 
                reader-position 
                cells 
                cell-pointer 
                movement 
                nested-left-brackets 
                nested-right-brackets]} app-state]
    (if (out-of-upper-sourcecode-bounds? app-state) (assoc app-state :terminated-due-to :reached-upper-sourcecode-bounds)
      (let [current-symbol (retrieve-current-symbol app-state)] ;; Read the current symbol
        (cond
          (looping-forward? app-state) (if (= \] current-symbol)
                                         (if (brackets-balanced? app-state) 
                                           ;; stop looping and start moving forward normally - reset nested brackets
                                           (assoc app-state
                                                  :reader-position (inc reader-position)
                                                  :movement :moving-forward
                                                  :nested-left-brackets 0
                                                  :nested-right-brackets 0)
                                           ;; continue looping and add nested right bracket
                                           (assoc app-state 
                                                  :reader-position (inc reader-position)
                                                  :movement :looping-forward
                                                  :nested-right-brackets (inc nested-right-brackets)))
                                         (if (= \[ current-symbol)
                                           ;; continue looping and add nested left bracket
                                           (assoc app-state
                                                  :reader-position (inc reader-position)
                                                  :movement :looping-forward
                                                  :nested-left-brackets (inc nested-left-brackets))
                                           ;; continue looping
                                           (assoc app-state 
                                                  :reader-position (inc reader-position)
                                                  :movement :looping-forward)))
          (looping-backward? app-state) (if (= \[ current-symbol)
                                          (if (brackets-balanced? app-state)
                                            ;; stop looping and start moving forward normally - reset nested brackets
                                            (assoc app-state
                                                   :reader-position (inc reader-position) 
                                                   :movement :moving-forward
                                                   :nested-left-brackets 0
                                                   :nested-right-brackets 0)
                                            ;; continue looping and add nested left bracket
                                            (assoc app-state 
                                                   :reader-position (dec reader-position)
                                                   :movement :looping-backward
                                                   :nested-left-brackets (inc nested-left-brackets)))
                                          (if (= \] current-symbol)
                                            ;; continue looping and add nested right bracket
                                            (assoc app-state 
                                                   :reader-position (dec reader-position)
                                                   :movement :looping-backward
                                                   :nested-right-brackets (inc nested-right-brackets))
                                            ;; continue looping 
                                            (assoc app-state 
                                                   :reader-position (dec reader-position)
                                                   :movement :looping-backward)))
          (= \+ current-symbol) (assoc app-state
                                       :reader-position (inc reader-position)
                                       :cells (assoc cells cell-pointer (inc (nth cells cell-pointer))) 
                                       :movement :moving-forward)
          (= \- current-symbol) (assoc app-state
                                       :reader-position (inc reader-position)
                                       :cells (assoc cells cell-pointer (dec (nth cells cell-pointer))) 
                                       :movement :moving-forward)
          (= \> current-symbol) (assoc app-state 
                                       :reader-position (inc reader-position)
                                       :cell-pointer (inc cell-pointer)
                                       :movement :moving-forward)
          (= \< current-symbol) (assoc app-state
                                       :reader-position (inc reader-position)
                                       :cell-pointer (dec cell-pointer)
                                       :movement :moving-forward)
          (= \. current-symbol) (do (print (char (nth cells cell-pointer)))
                                    (assoc app-state
                                           :reader-position (inc reader-position)
                                           :movement :moving-forward))
          (= \[ current-symbol) (if (= 0 (nth cells cell-pointer)) 
                                  (assoc app-state
                                         :reader-position (inc reader-position)
                                         :movement :looping-forward)
                                  (assoc app-state
                                         :reader-position (inc reader-position)
                                         :movement :moving-forward))
          (= \] current-symbol) (if (not (= 0 (nth cells cell-pointer)))
                                  (assoc app-state
                                         :reader-position (dec reader-position)
                                         :movement :looping-backward)
                                  (assoc app-state
                                         :reader-position (inc reader-position)
                                         :movement :moving-forward))
          (= \, current-symbol) (assoc app-state
                                       :reader-position (inc reader-position)
                                       :cells (assoc cells cell-pointer (int (first (read-line))))
                                       :movement :moving-forward)
          :else (assoc app-state
                       :reader-position (inc reader-position)
                       :movement :moving-forward)))))) ;; Moves reader-position forward if the current character is unknown

(defn interpret [src]
  (let [app-state {:src src
                   :reader-position 0
                   :cells (initialize-cells 200)
                   :cell-pointer 50
                   :movement :moving-forward
                   :nested-left-brackets 0
                   :nested-right-brackets 0
                   :terminated-due-to nil}]
    (loop [app-state app-state]
      (if (not (nil? (:terminated-due-to app-state)))
        app-state
        (recur (step app-state))))))

(defn -main
  "Time to interpret some Brainfuck"
  [& args]
  (do (interpret (first args))
      (println)))
