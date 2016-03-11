import os

from genderComputer import GenderComputer
from reader import UnicodeReader
from writer import UnicodeWriter

gc = GenderComputer(os.path.abspath('./nameLists'))

writer = UnicodeWriter(open(os.path.join('data/output.csv'), 'wb'))
reader = UnicodeReader(open(os.path.join('data/subsetGD.csv'), 'rb'))

_header = reader.next()
for rows in reader:
    row = rows[0].split(",")
    userId = row [0]
    login = row[1]
    name = row[2]
    company = row[3]
    location = row[4]
    email = row[5]
    createdTime = row[6]
    userType    = row[7]
    out = []
    out.append(userId)
    out.append(login)
    out.append(name)
    out.append(company)
    out.append(location)
    out.append(email)
    out.append(createdTime)
    out.append(userType)

    if (name != "" ):
        result = gc.resolveGender(name, location)

    out.append(result)
    s = ""
    for item in out:
        if (item == None):
            s += "None" + ","
        elif (item == True):
            s += "True" + ","
        elif (item == False):
            s += "False" + ","
        else:
            s += item + ","

    writer.writerow(s)
