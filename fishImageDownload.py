# -*- coding: utf-8 -*-
from selenium import webdriver
from selenium.webdriver.common.keys import Keys 
import time
import urllib.request
import os

#기본 다운로드
def download(name) :
    #폴더가 없으면 만들기
    if not os.path.isdir(f"{name}/"):
        os.makedirs(f"{name}/")

    #검색어로 이미지 페이지에 접속
    driver.get(f'https://www.google.com/search?q={name}&sxsrf=AJOqlzV-lyw8anLlzFcHKhEUb4ChnzkKBw:1679299540629&source=lnms&tbm=isch&sa=X&ved=2ahUKEwius9ykhur9AhV0XWwGHSpUA_kQ_AUoAXoECAEQAw&biw=1526&bih=726&dpr=1.25')
    
    #페이지가 로딩 될 때까지 기다림
    driver.implicitly_wait(10)

    last_height = driver.execute_script("return document.body.scrollHeight") #페이지의 높이를 저장

    cnt = 0 #사진 개수

    #스크롤을 더보기 버튼까지 눌러서 끝까지 내림
    while True:
      driver.execute_script("window.scrollTo(0, document.body.scrollHeight);") #스크롤을 끝까지 내려줌
      time.sleep(3)
      new_height = driver.execute_script("return document.body.scrollHeight") #스크롤을 내린 페이지의 높이를 저장
      if new_height == last_height: #스크롤을 내린 페이지와 이전 페이지의 길이를 비교해 더 이상 내릴 수 없어 같아진다면
        try:
            driver.find_element_by_css_selector('.mye4qd').click() #더보기 버튼 클릭
        except:
           break 
      last_height = new_height #새로운 높이를 마지막 높이에 저장 

    #이미지 다운
    for val in driver.find_elements_by_css_selector('.rg_i') : 
      try :
          if(cnt >= 300) : break #300장 다운받으면 반복문을 빠져나감
          src = val.get_attribute('src')
          urllib.request.install_opener(opener)
          urllib.request.urlretrieve(str(src), f'{name}/{name}_{cnt}.jpg') #이미지 링크로 이미지 저장
          cnt += 1
      except: pass

#한 종에 대해 상세하게 다운
def download2(name) :

    #검색어로 이미지 페이지에 접속
    driver.get(f'https://www.google.com/search?q={name}&sxsrf=AJOqlzV-lyw8anLlzFcHKhEUb4ChnzkKBw:1679299540629&source=lnms&tbm=isch&sa=X&ved=2ahUKEwius9ykhur9AhV0XWwGHSpUA_kQ_AUoAXoECAEQAw&biw=1526&bih=726&dpr=1.25')
    
    #페이지가 로딩 될 때까지 기다림
    driver.implicitly_wait(10)

    last_height = driver.execute_script("return document.body.scrollHeight")

    cnt = 0
    while True:
      driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
      time.sleep(3)
      new_height = driver.execute_script("return document.body.scrollHeight")
      if new_height == last_height:
        try:
            driver.find_element_by_css_selector('.mye4qd').click()
        except:
           print('나감')
           break
      last_height = new_height

    #이미지 다운
    for val in driver.find_elements_by_css_selector('.rg_i') :
      try :
          if(cnt >= 75) : break #75장 다운받으면 반복문을 빠져나감
          src = val.get_attribute('src')
          urllib.request.install_opener(opener)
          urllib.request.urlretrieve(str(src), f'소드테일/{name}_{cnt}.jpg')
          cnt += 1
      except: pass

    
#봇을 차단하는 것을 막기    
opener=urllib.request.build_opener() 
opener.addheaders=[('User-Agent','Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1941.0 Safari/537.36')]

options = webdriver.ChromeOptions()
options.add_experimental_option("excludeSwitches", ["enable-logging"])
driver = webdriver.Chrome(options=options)

#크롤링 하고 싶은 물고기 데이터
data = ["green swordtail", "black swordtail", "santa swordtail", "pink swordtail",
        "koi swordtail", "albino blood cauliflower swordtail", "red swordtail",
        "lemon swordtail", "orange swordtail", "double swordtail"]

#물고기 이름으로 사진 다운
for i in data :
   download2(i)
   
driver.close()




