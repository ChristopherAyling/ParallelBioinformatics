import java.io.File

def listGenBankRecordFiles(dir: String): List[String] = {
  val file = new File(dir)
  if (file.exists && file.isDirectory) {
    file.listFiles filter (_.isFile) map (_.getPath) toList
  } else {
    List(file.getPath)
  }
}

listGenBankRecordFiles("./Ecoli")

val dir = "C:\\Users\\chris\\Documents\\uni_projects\\CAB401\\sc\\hello-world\\Ecoli"
//val dir = "Ecoli"

val file = new File(dir)

file.getAbsoluteFile

file.isDirectory

file.listFiles()

def listGenBankRecordFiles(f: File): List[String] = {
  val these = f.listFiles
  these ++ these.filter(_.isDirectory).flatMap(listGenBankRecordFiles)
  these map (_.getAbsolutePath) toList
}

recursiveListFiles(file)

listGenBankRecordFiles(dir)

1 <= 2

