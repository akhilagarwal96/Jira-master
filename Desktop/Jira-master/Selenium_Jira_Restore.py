import selenium
from selenium import webdriver
from selenium.webdriver.common.keys import Keys

base_url = ""
login_id = ""
login_pwd = ""

driver = webdriver.Chrome(executable_path="C:/Users/akagarw/Desktop/Work/Scripts/JIRA/Automation/Jira Restore/restore1/drivers/chromedriver_win32/chromedriver.exe")
driver.maximize_window()

driver.implicitly_wait(20)

driver.get(base_url)

assert "" in driver.title

input_login = driver.find_element_by_id("login-form-username")
input_login.clear()
input_login.send_keys(login_id)

input_pwd = driver.find_element_by_id("login-form-password")
input_pwd.clear()
input_pwd.send_keys(login_pwd)

input_login.send_keys(Keys.RETURN)


admin = driver.find_element_by_id("admin_menu")
admin.click()

system = driver.find_element_by_id("admin_system_menu")
system.click()

admin_pwd = driver.find_element_by_id("login-form-authenticatePassword")
admin_pwd.send_keys(login_pwd)
admin_pwd.send_keys(Keys.RETURN)

restore = driver.find_element_by_id("restore_data")
restore.click()