import groovy.io.FileType

def folder = "E:\\sms\\"
def dir = new File(folder)
def csv = ""
dir.eachFileRecurse(FileType.FILES) { file ->
    println file.name
    def filename = file.name
    def String szDate = (filename =~ "(\\d){4}-(\\d){2}-(\\d){2} (\\d){4}")[0][0]
// println "szDate: $szDate"
    def date = Date.parse("yyyy-MM-dd hhmm", szDate)
    def String phoneNumber
    def String sender
    if (filename.startsWith("+")) {
        phoneNumber = (filename =~ "\\+{0,1}\\d+")[0]
        sender = ""
    } else {
        if ((filename =~ "\\+{1}\\d+").size() > 0) {
            phoneNumber = (filename =~ "\\+{0,1}\\d+")[0]
            sender = filename.substring(0, filename.indexOf(phoneNumber) - 2).trim()
        } else {
            try {
                phoneNumber = (filename =~ "^\\d+")[0]
                sender = ""
            } catch (Exception e) {
                phoneNumber = ""
                sender = filename.substring(0, filename.indexOf(" 201"))
            }
        }

    }
// println "phoneNumber: $phoneNumber"
// println "sender: $sender"

    def fileText = file.text
    def String sms
    def varWrote = "($phoneNumber) wrote:"
    def varEcrit = "($phoneNumber) a =C3=A9crit :"
    if (fileText.contains(varWrote)) {
        sms = fileText.substring(fileText.indexOf(varWrote) + varWrote.length())
    } else if (fileText.contains(varEcrit)) {
        sms = fileText.substring(fileText.indexOf(varEcrit) + varEcrit.length())
    } else sms = fileText
    sms = sms.trim()
    ["=C3=A9": "e",
     "=C3=A0": "a",
     "=C3=A8": "e",
     "=C3=A7": "c",
     "=C3=B4": "o",
     "\"" : "\\\""
    ].each { search, replace ->
        sms = sms.replaceAll(search, replace)
    }
// sms = StringEscapeUtils.escapeCsv(sms)
// println "sms: $sms"
    csv += date.format("yyyy/MM/dd") + "," + date.format("hh:MM") + ",'$phoneNumber,'$sender,\"$sms\"\r\n"
}
//println csv
def output = new File(folder + "SMShare.csv")
output.text = csv
