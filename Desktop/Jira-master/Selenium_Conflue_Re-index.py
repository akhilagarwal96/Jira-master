import selenium
from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as cond
from selenium.webdriver.common.keys import Keys

import config

base_url = ""
login_id = config.username
login_pwd = config.password

driver = webdriver.Chrome(executable_path="C:/Users/akagarw/Desktop/Work/Scripts/JIRA/Automation/Jira Restore/restore1/drivers/chromedriver_win32/chromedriver.exe")
driver.maximize_window()
driver.implicitly_wait(10)
driver.get(base_url)

input_login = driver.find_element_by_id("os_username")
input_login.clear()
input_login.send_keys(login_id)

input_pwd = driver.find_element_by_id("os_password")
input_pwd.clear()
input_pwd.send_keys(login_pwd)

input_pwd.send_keys(Keys.RETURN)

config = driver.find_element_by_id("admin-menu-link")
config.click()

admin = driver.find_element_by_id("administration-link")
admin.click()

admin_pwd = driver.find_element_by_id("password")
admin_pwd.clear()
admin_pwd.send_keys(login_pwd)
admin_pwd.send_keys(Keys.RETURN)

# content_index = driver.find_element_by_xpath('//a[@href="/admin/search-indexes.action"]')
content_index = driver.find_element_by_link_text("Content Indexing")
content_index.click()

btn_rebuild = driver.find_elements_by_id("build-search-index-button")
# btn_rebuild.click()

perc = driver.find_element_by_id("search-index-task-progress-container-percent-complete-text")
print(perc.text)