curl -v -H "Content-Type: application/json" -X PUT -d "{'user':'bob@web.de','password':'halloIchbinBob','pseudonym':'bob'}" localhost:5001/login
curl -v -H "Content-Type: application/json" -X PUT -d "{'token':'Hier Token einfügen','pseudonym':'bob'}" localhost:5001/auth
