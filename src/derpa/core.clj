(ns derpa.core
  (:gen-class))

(defn initialize-cells [number] (vec (repeat number 0)))

(defn interpret [src]
  (loop [src src
         reader-position 0
         cells (initialize-cells 200)
         cell-pointer 50
         movement :moving-forward
         nested-left-brackets 0
         nested-right-brackets 0]
    (if (= reader-position (count src)) cells ;; Terminal case - stop reading
      (let [current-symbol (nth src reader-position)] ;; Read the current symbol
        (cond
          (= :looping-forward movement) (if (= \] current-symbol)
                                          (if (= nested-left-brackets nested-right-brackets)
                                            (recur src ;; stop looping and start moving forward normally - reset nested brackets
                                                   (inc reader-position)
                                                   cells
                                                   cell-pointer
                                                   :moving-forward
                                                   0
                                                   0)
                                            (recur src ;; continue looping and add nested right bracket
                                                   (inc reader-position)
                                                   cells
                                                   cell-pointer
                                                   :looping-forward
                                                   nested-left-brackets
                                                   (inc nested-right-brackets)))
                                          (if (= \[ current-symbol)
                                                   (recur src ;; continue looping and add nested left bracket
                                                          (inc reader-position)
                                                          cells
                                                          cell-pointer
                                                          :looping-forward
                                                          (inc nested-left-brackets)
                                                          nested-right-brackets)
                                                   (recur src ;; continue looping
                                                          (inc reader-position)
                                                          cells
                                                          cell-pointer
                                                          :looping-forward
                                                          nested-left-brackets
                                                          nested-right-brackets)))
                                                   (= :looping-backward movement) (if (= \[ current-symbol)
                                                                                           (if (= nested-left-brackets nested-right-brackets)
                                                                                             (recur src ;; stop looping and start moving forward normally - reset nested brackets
                                                                                                    (inc reader-position)
                                                                                                    cells
                                                                                                    cell-pointer
                                                                                                    :moving-forward
                                                                                                    0
                                                                                                    0)
                                                                                             (recur src ;; continue looping and add nested left bracket
                                                                                                    (dec reader-position)
                                                                                                    cells
                                                                                                    cell-pointer
                                                                                                    :looping-backward
                                                                                                    (inc nested-left-brackets)
                                                                                                    nested-right-brackets))
                                                                                           (if (= \] current-symbol)
                                                                                             (recur src ;; continue looping and add nested right bracket
                                                                                                    (dec reader-position)
                                                                                                    cells
                                                                                                    cell-pointer
                                                                                                    :looping-backward
                                                                                                    nested-left-brackets
                                                                                                    (inc nested-right-brackets))
                                                                                             (recur src ;; continue looping 
                                                                                                    (dec reader-position)
                                                                                                    cells
                                                                                                    cell-pointer
                                                                                                    :looping-backward
                                                                                                    nested-left-brackets
                                                                                                    nested-right-brackets)))
                                                   (= \+ current-symbol) (recur src
                                                                                           (inc reader-position)
                                                                                           (assoc cells cell-pointer (inc (nth cells cell-pointer))) 
                                                                                           cell-pointer
                                                                                           :moving-forward
                                                                                           nested-left-brackets
                                                                                           nested-right-brackets)
                                                   (= \- current-symbol) (recur src
                                                                                           (inc reader-position)
                                                                                           (assoc cells cell-pointer (dec (nth cells cell-pointer))) 
                                                                                           cell-pointer
                                                                                           :moving-forward
                                                                                           nested-left-brackets
                                                                                           nested-right-brackets)
                                                   (= \> current-symbol) (recur src
                                                                                           (inc reader-position)
                                                                                           cells
                                                                                           (inc cell-pointer)
                                                                                           :moving-forward
                                                                                           nested-left-brackets
                                                                                           nested-right-brackets)
                                                   (= \< current-symbol) (recur src
                                                                                           (inc reader-position)
                                                                                           cells
                                                                                           (dec cell-pointer)
                                                                                           :moving-forward
                                                                                           nested-left-brackets
                                                                                           nested-right-brackets)
                                                   (= \. current-symbol) (do (print (char (nth cells cell-pointer)))
                                                                                      (recur src
                                                                                             (inc reader-position)
                                                                                             cells
                                                                                             cell-pointer
                                                                                             :moving-forward
                                                                                             nested-left-brackets
                                                                                             nested-right-brackets))
                                                   (= \[ current-symbol) (if (= 0 (nth cells cell-pointer)) 
                                                                                      (recur src
                                                                                             (inc reader-position)
                                                                                             cells
                                                                                             cell-pointer
                                                                                             :looping-forward
                                                                                             nested-left-brackets
                                                                                             nested-right-brackets)
                                                                                      (recur src
                                                                                             (inc reader-position)
                                                                                             cells
                                                                                             cell-pointer
                                                                                             :moving-forward
                                                                                             nested-left-brackets
                                                                                             nested-right-brackets))
                                                        (= \] current-symbol) (if (not (= 0 (nth cells cell-pointer)) )
                                                                                           (recur src
                                                                                                  (dec reader-position)
                                                                                                  cells
                                                                                                  cell-pointer
                                                                                                  :looping-backward
                                                                                                  nested-left-brackets
                                                                                                  nested-right-brackets)
                                                                                           (recur src
                                                                                                  (inc reader-position)
                                                                                                  cells
                                                                                                  cell-pointer
                                                                                                  :moving-forward
                                                                                                  nested-left-brackets
                                                                                                  nested-right-brackets))
(= \, current-symbol) (recur src
                                        (inc reader-position)
                                        (assoc cells cell-pointer (int (first (read-line))))
                                        cell-pointer
                                        :moving-forward
                                        nested-left-brackets
                                        nested-right-brackets)
:else (recur src
             (inc reader-position)
             cells
             cell-pointer
             :moving-forward 
             nested-left-brackets
             nested-right-brackets)))))) ;; Moves reader-position forward if the current character is unknown
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (do (interpret (first args))
    (println)))
