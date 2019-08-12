![](http://james.am/inactiveheader.png)

## What is this?
This is a selenium based project that will automate logging into your Virgin Pulse account and complete tasks to give you points.

This will earn you the following points:

**Daily**  
- Daily Cards - 40  
- Track your Healthy Habits - 30  
- Complete a Whil session - 20  

**Monthly**  
- Complete 10 & 20 daily cards in a month - 300  
- Complete 10 & 20 whil sessions in a month - 300  
- Track 10 & 20 Healthy Habits in a month - 300  

In total, it should give you ~3,600 points per month or ~10,800 per quarter.

### The following features are complete:

- Daily Cards
- "Getting Active" habits
- Whil  

### The following are still being developed:
- Support for all Healthy Habits (currently only supports "Getting Active" habits)  
- Step tracking  

### How to run:

java -jar pulse.jar emailaddress password chromedriverLocation

**For example:**
java -jar pulse.jar user@domain.com Password1 C:\\pathto\chromedriver

### Pre-requisites:
To get a copy of chromedriver, go here and download whatever one is relevant for your chrome version:
http://chromedriver.chromium.org/downloads
