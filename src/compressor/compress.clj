(ns compressor.compress
  (:require [clojure.java.io :refer [file output-stream]]
            [compressor.compress :as compress])
  (:import [javax.imageio ImageIO IIOImage ImageWriteParam]
           [org.apache.commons.compress.compressors CompressorStreamFactory]
           [org.apache.commons.compress.archivers.tar TarArchiveOutputStream]
           [org.apache.commons.io FilenameUtils]
           [org.apache.commons.compress.utils IOUtils]))

(defn file-extension
  [file]
  (second (re-find #"([^.][a-zA-Z0-9]+)$" file)))

(def archive-extensions
  {"bzip2"      ".tar.bz2"
   "deflate"    ".tar.gz"
   "gz"         ".tar.gz"
   "lz4-framed" ".tar.lz4"
   "lzma"       ".tar.lzma"
   "xz"         ".tar.xz"})

(defn create-archive-name
  [output compressor]
  (let [extension   (get archive-extensions compressor)
        output-file (str output extension)]
    output-file))

(defn- relativise-path
  [base path]
  (let [f    (file base)
        uri      (.toURI f)
        relative (.relativize uri (-> path file .toURI))]
    (.getPath relative)))

(defn create-archive
  [input-files & {:keys [output compressor]
                  :or {output input-files compressor "gz"}}]
  (let [output-name       (create-archive-name output compressor)
        fo                (output-stream output-name)
        compressor-stream (.createCompressorOutputStream (CompressorStreamFactory.) compressor fo)
        tar-output-stream (TarArchiveOutputStream. compressor-stream)
        file-list         (mapv str (file-seq (file input-files)))]
    (doseq [input-name file-list]
      (let [folder? (.isDirectory (file input-name))]
        (doseq [f (if folder? (file-seq (file input-name)) [(file input-name)])]
            (let [entry-name (relativise-path (FilenameUtils/getPath  input-name) (-> f .getPath))
                  entry (.createArchiveEntry tar-output-stream f entry-name)]
              (.putArchiveEntry tar-output-stream entry)
              (when (.isFile f)
                (IOUtils/copy f tar-output-stream))
              (.closeArchiveEntry tar-output-stream)))))
    (.finish tar-output-stream)
    (.close tar-output-stream)
    output-name))

(defn compress-img
  [img & {:keys [quality new metadata]
          :or {quality 0.7 new img metadata false}}]
  (let [ext    (file-extension img)
        reader (.next (ImageIO/getImageReadersByFormatName ext))
        writer (.next (ImageIO/getImageWritersByFormatName ext))]
    (with-open [input-stream (ImageIO/createImageInputStream (file img))
                output-stream (ImageIO/createImageOutputStream (file new))]
      (.setInput reader input-stream)
      (.setOutput writer output-stream)
      (let [image (if metadata (IIOImage. (.read reader 0) nil (.getImageMetadata reader 0)) (IIOImage. (.read reader 0) nil nil))
            params (doto (.getDefaultWriteParam writer)
                     (.setCompressionMode ImageWriteParam/MODE_EXPLICIT)
                     (.setCompressionQuality (read-string quality))
                     (.setOptimizeHuffmanTables true))]
        (.write writer nil image params))
      (.dispose reader)
      (.dispose writer))))
