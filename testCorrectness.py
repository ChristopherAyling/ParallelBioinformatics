import json

with open("./SeqOutput.json") as f:
    seq_json = json.load(f)

with open("./ParaOutput.json") as f:
    para_json = json.load(f)

print(seq_json, "\n", para_json)
print(seq_json == para_json)
