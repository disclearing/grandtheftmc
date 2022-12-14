#!/usr/bin/python

# local imports

# python modules
import MySQLdb
import string
import time

DB_HOST = "localhost"
DB_USER = "root"
DB_PASS = ""
DB_NAME = ""
# number of queries per interval
MAX_QUERY_TICK = 1000000000
# how long in seconds we sleep for
TICK_SLEEP = 1

SERVER_KEY = "GTM0"
QUERY_GET_REG_HOUSES = ''' SELECT uuid, houseId FROM gtm0_houses; '''
QUERY_GET_REG_CHESTS = '''SELECT uuid, houseId, chestId, contents FROM gtm0_houses_chests; '''

# open MySQL connection
db = MySQLdb.connect(host=DB_HOST, user=DB_USER, passwd=DB_PASS, db=DB_NAME)
# grab cursor
cur = db.cursor()

# print out variables
print("MAX_QUERY_TICK = " + str(MAX_QUERY_TICK))
print("TICK_SLEEP = " + str(TICK_SLEEP))
print("SERVER_KEY = " + str(SERVER_KEY))
print("QUERY_GET_REG_HOUSES = " + str(QUERY_GET_REG_HOUSES))
print("QUERY_GET_REG_CHESTS = " + str(QUERY_GET_REG_CHESTS))

def get_regular_houses():
	'''
	Get all the regular houses known on the network.

	Return:
		A list of pairs in the form of (uuid, house_num) for all the regular houses.
	'''

	# # grab cursor
	# cur = db.cursor()
	cur.execute(QUERY_GET_REG_HOUSES)

	houses = []
	for tup in cur:

		uuid = string.replace(str(tup[0]), "-", "")
		house_num = int(tup[1])

		houses.append((uuid, house_num))

	# commit query
	# db.commit()
	# cur.close()
	
	return houses

def get_regular_chests():
	'''
	Get all the regular chests information on the network.

	Return:
		A list of tuples in the form of (uuid, house_num, chest_id, contents)
		that represent data for a chest.
	'''

	# # grab cursor
	# cur = db.cursor()
	cur.execute(QUERY_GET_REG_CHESTS)

	chests = []
	for tup in cur:
		uuid = string.replace(str(tup[0]), "-", "")
		house_num = int(tup[1])
		chest_id = int(tup[2])
		contents = str(tup[3])

		chests.append((uuid, house_num, chest_id, contents))

	# # commit query
	# db.commit()
	# cur.close()
	
	return chests

def find_house_id(house_num, server_key, premium):
	'''
	Find the id of the house that was generated by the database.

	Args:
		house_num: The number of the house
		server_key: The key of server that this house is for
		premium: Whether or not this is a premium house

	Return:
		A list of tuples in the form of (uuid, house_num, chest_id, contents)
		that represent data for a chest.
	'''

	# grab cursor
	# cur = db.cursor()
	query = '''SELECT id FROM gtm_house WHERE house_num=%s AND server_key=%s AND premium=%s '''
	data = (int(house_num), str(server_key), bool(premium))
	cur.execute(query, data)

	house_id = None
	for tup in cur:
		if len(tup) > 0:
			house_id = int(tup[0])

	# # commit query
	# db.commit()
	# cur.close()

	return house_id

# CREATE TABLE IF NOT EXISTS gtm_house_chest (
# house_id INT NOT NULL, 
# uuid BINARY(16) NOT NULL, 
# chest_id INT NOT NULL, 
# content BLOB DEFAULT NULL, 
# PRIMARY KEY (house_id, uuid),
# INDEX(uuid), 
# FOREIGN KEY (house_id) REFERENCES gtm_house(id) ON DELETE CASCADE
# );

def create_house_chest(house_id, uuid, chest_id, content):
	'''
	Create chest information for the respective house.

	Args:
		house_id: The id of the house
		uuid: The uuid of the user for the respective house
		chest_id: The id of the chest
		content: The contents of the chest
	'''
	# # grab cursor
	# cur = db.cursor()
	query = '''INSERT INTO gtm_house_chest (house_id, uuid, chest_id, content) VALUES (%s, UNHEX(%s), %s, %s);'''
	data = (int(house_id), str(uuid), int(chest_id), str(content))
	cur.execute(query, data)

	# # commit query
	# db.commit()
	# cur.close()

def create_house_user(house_id, uuid, is_owner):
	'''
	Create a new house user record in the database for the specified house.

	Args:
		house_id: The id of the house
		uuid: The uuid of the user for the house
		is_owner: Whether the user owns the house
	'''

	# # grab cursor
	# cur = db.cursor()

	query = ''' INSERT INTO gtm_house_user (house_id, uuid, is_owner) VALUES (%s, UNHEX(%s), %s); '''
	data = (int(house_id), str(uuid), bool(is_owner))
	cur.execute(query, data)

	# # commit query
	# db.commit()
	# cur.close()

#########
# BELOW IS THE ACTUAL SCRIPT LOGIC
#########

print('Running script...')
start_time = time.time()

# limit the amount of queries to the database per interval
tick = 0

houses = get_regular_houses()
houses_created = 0
for uuid, house_num in houses:
	tick += 1

	# find generated house id
	house_id = find_house_id(house_num=house_num, server_key=SERVER_KEY, premium=0)
	if house_id is not None:

		# create the user in the house relation
		create_house_user(house_id=house_id, uuid=uuid, is_owner=True)
		houses_created += 1

	if tick > MAX_QUERY_TICK:
		tick = 0
		time.sleep(TICK_SLEEP)

# get all the houses in old tables
chests = get_regular_chests()
chests_created = 0
for uuid, house_num, chest_id, contents in chests:
	tick += 1

	# find generated house id
	house_id = find_house_id(house_num=house_num, server_key=SERVER_KEY, premium=0)
	if house_id is not None:

		try:
			# create house data in new table
			create_house_chest(house_id=house_id, uuid=uuid, chest_id=chest_id, content=contents)
			chests_created += 1
		except Exception as e:
			print(e)
			print('Error for house_id=' + str(house_id) + ', uuid=' + str(uuid) + ', chest_id=' + str(chest_id))

	if tick > MAX_QUERY_TICK:
		tick = 0
		time.sleep(TICK_SLEEP)

# commit query
db.commit()
cur.close()

run_time = time.time() - start_time	
print('Script is now complete! (' + str(run_time) + ' secs)')
print('Number of houses found: ' + str(len(houses)))
print('Number of houses transposed: ' + str(houses_created))
print('Number of chests found: ' + str(len(chests)))
print('Number of chests transposed: ' + str(chests_created))

# close the connection
db.close()