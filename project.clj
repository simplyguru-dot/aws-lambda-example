(defproject aws-lambda-skeleton "0.1.0-SNAPSHOT"
  :description "Simple AWS Lambda example"
  :url "https://github.com/lowl4tency/aws-lambda-exmaple"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                  [org.clojure/data.json "0.2.6"]
                 [org.clojure/tools.logging "0.3.1"]
                 [http-kit "2.1.18"]
                 [amazonica "0.3.52" :exclusions [com.amazonaws/aws-java-sdk]]
                 [com.amazonaws/aws-java-sdk-core "1.10.62"]
                 [com.amazonaws/aws-lambda-java-core "1.1.0"]
                 [com.amazonaws/aws-java-sdk-ec2 "1.10.62"
                  :exclusions [joda-time]]
                 [com.amazonaws/aws-lambda-java-events "1.1.0"
                  :exclusions [com.amazonaws/aws-java-sdk-dynamodb
                               com.amazonaws/aws-java-sdk-kinesis
                               com.amazonaws/aws-java-sdk-cognitoidentity
                               com.amazonaws/aws-java-sdk-sns
                               com.amazonaws/aws-java-sdk-s3]]]
  :resource-paths ["resources"]
  :aot :all
  :profiles {:uberjar {:aot :all}})

