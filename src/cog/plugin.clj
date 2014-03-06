(ns cog.plugin
  (:require leiningen.cog
            leiningen.ring.server
            robert.hooke))

(defn hooks
  []
  (robert.hooke/add-hook
    #'leiningen.ring.server/server-task
    #'leiningen.cog/watcher-task))
