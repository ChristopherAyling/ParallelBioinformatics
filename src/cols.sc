import qut._


val dir = "C:\\Users\\chris\\Documents\\uni_projects\\CAB401\\sc\\hello-world\\Ecoli"

val GenBankFiles = Sequential.ListGenbankFiles(dir)

val GBF = GenBankFiles.get(0)

val parsed = Sequential.Parse(GBF)

var gbfs: Seq[GenbankRecord] = Seq()

GenBankFiles forEach(file => {
  gbfs = gbfs ++ Seq(Sequential.Parse(file))
})

gbfs
