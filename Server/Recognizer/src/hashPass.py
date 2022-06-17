import hashlib

str2hash = "admin"

result = hashlib.md5(str2hash.encode())

result = result.hexdigest()

print(result)