(ns derpa.core
  (:gen-class))

(defn initialize-cells [number] (vec (repeat number 0)))

(defn out-of-sourcecode-bounds? [app-state]
  (= (:reader-position app-state) (count (:src app-state)))) ;;TODO currently only checks for upper bounds

(defn retrieve-current-symbol [app-state]
  (nth (:src app-state) (:reader-position app-state)))

(defn looping-forward? [app-state]
  (= :looping-forward (:movement app-state)))

(defn brackets-balanced? [app-state]
  (= (:nested-left-brackets app-state) (:nested-right-brackets app-state)))

(defn looping-backward? [app-state]
  (= :looping-backward (:movement app-state)))

(defn interpret [src]
  (let [app-state {:src src
                   :reader-position 0
                   :cells (initialize-cells 200)
                   :cell-pointer 50
                   :movement :moving-forward
                   :nested-left-brackets 0
                   :nested-right-brackets 0}]
    (loop [app-state app-state]
      (if (out-of-sourcecode-bounds? app-state) app-state ;; Terminal case - stop reading
        (let [current-symbol (retrieve-current-symbol app-state)] ;; Read the current symbol
          (cond
            (looping-forward? app-state) (if (= \] current-symbol)
                                           (if (brackets-balanced? app-state) 
                                             ;; stop looping and start moving forward normally - reset nested brackets
                                             (recur (assoc app-state
                                                           :reader-position (inc (:reader-position app-state))
                                                           :movement :moving-forward
                                                           :nested-left-brackets 0
                                                           :nested-right-brackets 0))
                                             ;; continue looping and add nested right bracket
                                             (recur (assoc app-state 
                                                           :reader-position (inc (:reader-position app-state))
                                                           :movement :looping-forward
                                                           :nested-right-brackets (inc (:nested-right-brackets app-state)))))
                                           (if (= \[ current-symbol)
                                             ;; continue looping and add nested left bracket
                                             (recur (assoc app-state
                                                           :reader-position (inc (:reader-position app-state))
                                                           :movement :looping-forward
                                                           :nested-left-brackets (inc (:nested-left-brackets app-state))))
                                             ;; continue looping
                                             (recur (assoc app-state 
                                                           :reader-position (inc (:reader-position app-state))
                                                           :movement :looping-forward))))
            (looping-backward? app-state) (if (= \[ current-symbol)
                                            (if (brackets-balanced? app-state)
                                              ;; stop looping and start moving forward normally - reset nested brackets
                                              (recur (assoc app-state
                                                            :reader-position (inc (:reader-position app-state)) 
                                                            :movement :moving-forward
                                                            :nested-left-brackets 0
                                                            :nested-right-brackets 0))
                                              ;; continue looping and add nested left bracket
                                              (recur (assoc app-state 
                                                            :reader-position (dec (:reader-position app-state))
                                                            :movement :looping-backward
                                                            :nested-left-brackets (inc (:nested-left-brackets app-state)))))
                                            (if (= \] current-symbol)
                                              ;; continue looping and add nested right bracket
                                              (recur (assoc app-state 
                                                            :reader-position (dec (:reader-position app-state))
                                                            :movement :looping-backward
                                                            :nested-right-brackets (inc (:nested-right-brackets app-state))))
                                              ;; continue looping 
                                              (recur (assoc app-state 
                                                            :reader-position (dec (:reader-position app-state))
                                                            :movement :looping-backward))))
            (= \+ current-symbol) (recur (assoc app-state
                                                :reader-position (inc (:reader-position app-state))
                                                :cells (assoc (:cells app-state) (:cell-pointer app-state) (inc (nth (:cells app-state) (:cell-pointer app-state)))) 
                                                :movement :moving-forward))
            (= \- current-symbol) (recur (assoc app-state
                                                :reader-position (inc (:reader-position app-state))
                                                :cells (assoc (:cells app-state) (:cell-pointer app-state) (dec (nth (:cells app-state) (:cell-pointer app-state)))) 
                                                :movement :moving-forward))
            (= \> current-symbol) (recur (assoc app-state 
                                                :reader-position (inc (:reader-position app-state))
                                                :cell-pointer (inc (:cell-pointer app-state))
                                                :movement :moving-forward))
            (= \< current-symbol) (recur (assoc app-state
                                                :reader-position (inc (:reader-position app-state))
                                                :cell-pointer (dec (:cell-pointer app-state))
                                                :movement :moving-forward))
            (= \. current-symbol) (do (print (char (nth (:cells app-state) (:cell-pointer app-state))))
                                      (recur (assoc app-state
                                                    :reader-position (inc (:reader-position app-state))
                                                    :movement :moving-forward)))
            (= \[ current-symbol) (if (= 0 (nth (:cells app-state) (:cell-pointer app-state))) 
                                    (recur (assoc app-state
                                                  :reader-position (inc (:reader-position app-state))
                                                  :movement :looping-forward))
                                    (recur (assoc app-state
                                                  :reader-position (inc (:reader-position app-state))
                                                  :movement :moving-forward)))
            (= \] current-symbol) (if (not (= 0 (nth (:cells app-state) (:cell-pointer app-state))))
                                    (recur (assoc app-state
                                                  :reader-position (dec (:reader-position app-state))
                                                  :movement :looping-backward))
                                    (recur (assoc app-state
                                                  :reader-position (inc (:reader-position app-state))
                                                  :movement :moving-forward)))
            (= \, current-symbol) (recur (assoc app-state
                                                :reader-position (inc (:reader-position app-state))
                                                :cells (assoc (:cells app-state) (:cell-pointer app-state) (int (first (read-line))))
                                                :movement :moving-forward))
            :else (recur (assoc app-state
                                :reader-position (inc (:reader-position app-state))
                                :movement :moving-forward)))))))) ;; Moves reader-position forward if the current character is unknown

(defn -main
  "Time to interpret some Brainfuck"
  [& args]
  (do (interpret (first args))
      (println)))
