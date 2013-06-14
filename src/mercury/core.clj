(ns mercury.core
  (:import [com.tinkerpop.gremlin.groovy Gremlin]
           [org.codehaus.groovy.tools.shell Groovysh IO
            InteractiveShellRunner] 
           [com.tinkerpop.gremlin.groovy.console ErrorHookClosure NullResultHookClosure PromptClosure ResultHookClosure]
           [jline History]))

(def history-file  ".gremlin_mercury_history")
(def input-prompt  "gremlin>")
(def result-prompt "==>")

(defn print-gremlin []
  (println)
  (println "         \\,,,/")
  (println "         (o o)")
  (println "-----oOOo-(_)-oOOo-----"))

(defn start-repl []
  (println "Starting up Mercury...")
  (let [groovy (new Groovysh)
        io (IO. System/in System/out System/err)
        imports (concat (com.tinkerpop.gremlin.Imports/getImports)
                        ["com.tinkerpop.gremlin.Tokens.T"
                         "com.tinkerpop.gremlin.groovy.*"])]
    ;; Import everything that's needed into groovy. 
    (.setResultHook groovy (NullResultHookClosure. groovy))
    (doseq [import imports]
      (.execute groovy (str "import " import)))

    ;;Set the repl up for interaction
    (.setResultHook groovy (ResultHookClosure. groovy io result-prompt))
    (.setHistory groovy (History.))

    (let [runner (InteractiveShellRunner. groovy (PromptClosure. groovy input-prompt))]
      (.setErrorHandler runner (ErrorHookClosure. runner io))
      ;;Set up history file
      (try
        (.setHistory runner (History. (java.io.File. 
                                       (str (System/getProperty "user.home") "/" history-file))))
        (catch Exception e
            (println (str "Unable to create history file: " history-file))))
      
      ;;Load gremlin
      (Gremlin/load)
      (print-gremlin)
      ;;Start it up 
      (.run runner))))



(defn -main []
  (start-repl))

