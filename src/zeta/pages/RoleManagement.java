package zeta.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import zeta.utilities.Report;
import zeta.utilities.RunFactory;
import zeta.utilities.TestSettings;
import zeta.utilities.ZetaTestDriver;
import zeta.utilities.ZetaTestDriver.RunDetails;

public class RoleManagement extends ZetaCMS{

	public RoleManagement(WebDriver browser, Report report, RunDetails threadRun){
		super(browser, report, threadRun);
	}
	
	private static final String AddUsers = "//a[contains(@data-modal-link, '/role-management/users/search-users-modal/')]";
	/**
	 * @author nwisnewski
	 * Removes testingacct_02 from all groups.
	 * @return
	 * @throws Exception
	 */
	public RoleManagement removeFromGroup() throws Exception{
		zetaSearch("testingacct_02", "Role Management");
		clickElementBy("css", EditAPW);
		Thread.sleep(1000);
		String removeFromGroup = "//td/a[@data-posturl='/api/role-management/users/removeUserFromGroup/']";
		String userInfo = "//div[@data-xtype='usermgmt']";
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(userInfo)));
		} catch (Exception e) {
			waitFail(e);
		}
		if(!tempElementBy("xpath", userInfo).getText().equalsIgnoreCase("user not part of any group")){
			try {
				wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(removeFromGroup)));
			} catch (Exception e) {
				waitFail(e);
			}
			List<WebElement> groups = browser.findElements(By.xpath(removeFromGroup));
			for(WebElement remove:groups){
				Thread.sleep(500);
				remove.click();
			}
			Thread.sleep(3000);
		}
		report.runStep(tempElementBy("xpath", userInfo).getText().equalsIgnoreCase("user not part of any group")
				, "TestingAcct_02 has been removed from all groups, to be assigned to group created in the next step.");
		return this;
	}
	/**
	 * @author nwisnewski
	 * Creates a new group
	 * @param groupName
	 * @return
	 * @throws Exception
	 */
	public RoleManagement addNewGroup(String groupName) throws Exception{
		String groupTab="//div[@id='role-management-tab-ribbon']/ul/li/a[@href='#groups_browse']";
		String createNewGroup = "//a[@data-modal-link='/role-management/users/create-group-modal']";
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(groupTab)));
		} catch (Exception e) {
			waitFail(e);
		}
		Thread.sleep(500);
		clickElementBy("xpath", groupTab);
		Thread.sleep(500);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(createNewGroup)));
		} catch (Exception e) {
			waitFail(e);
		}
		Thread.sleep(500);
		clickElementBy("xpath", createNewGroup);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("create_group_name")));
		} catch (Exception e) {
			waitFail(e);
		}
		tempElementBy("id", "create_group_name").sendKeys(groupName);
		Thread.sleep(500);
		clickElementBy("css", ".btn.btn-default.new-group-create");
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AddUsers)));
		} catch (Exception e) {
			waitFail(e);
		}
		report.runStep(isElementPresentBy("xpath", AddUsers), "New group '"+groupName+"' was created successfully.");
		return this;
	}
	/**
	 * @author nwisnewski
	 * Adds testingacct_02 to a group
	 * @param groupName
	 * @return
	 * @throws InterruptedException
	 */
	public RoleManagement addUserToGroup(String groupName) throws InterruptedException{
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(AddUsers)));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("xpath", AddUsers);
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search_user_username")));
		} catch (Exception e) {
			waitFail(e);
		}
		tempElementBy("id", "search_user_username").sendKeys("TestingAcct_02");
		Thread.sleep(500);
		clickElementBy("css", ".btn.btn-default.new-user-search");
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".grid-add-new-user")));
		} catch (Exception e) {
			waitFail(e);
		}
		clickElementBy("css", ".grid-add-new-user");
		Thread.sleep(3000);
		clickElementBy("xpath", "//button[@data-dismiss='modal']");
		Thread.sleep(1000);
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(AddUsers)));
		} catch (Exception e) {
			waitFail(e);
		}
		Thread.sleep(500);
		String newUser = "//table[contains(@id, 'zeta_groupusers_table')]/tbody/tr/td[text()='TestingAcct_02']";
		if(ZetaTestDriver.project.equalsIgnoreCase("nextgen")){
			newUser = "//table[contains(@id, 'zeta_groupusers_table')]/tbody/tr/td[text()='testingacct_02']";
		}
		report.runStep(isElementPresentBy("xpath", newUser)
				, "TestingAcct_02 was added to the group '"+groupName+"'.");
		return this;
	}
	/**
	 * @author nwisnewski
	 * Sets the Zeta access permissions for the group
	 * @param groupName - title of group created
	 * @param permissions - {Label of the row/ Zeta page, id in the name and value of the html tag of the input tag for all checkboxes in that row}
	 * @return
	 * @throws Exception
	 */
	public RoleManagement setPermissions(String groupName, String[][] permissions) throws Exception{
		//unique id for each group needs to get retrieved from html
		String groupId = parseGroupId();
		System.out.println(groupId);
		int y = tempElementBy("css", ".btn.btn-primary.save-all-permissions.role-permission-grid-btn").getLocation().getY()-300;
		((JavascriptExecutor)browser).executeScript(" window.scrollBy(0, "+y+");");
		for(int i=0; i<permissions.length; i++){
			Thread.sleep(500);
			//checks permissions box for a given row and saves that row
			checkPermissionChkBox(permissions[i][0], permissions[i][1], groupId);
		}
		/*((JavascriptExecutor) browser).executeScript("arguments[0].scrollIntoView(true);", tempElementBy("css", ".btn.btn-primary.save-all-permissions.role-permission-grid-btn"));
		clickElementBy("css", ".btn.btn-primary.save-all-permissions.role-permission-grid-btn");*/
		return this;
	}
	/**
	 * @author nwisnewski
	 * Check Create, Read, Update, and Delete checkboxes for a given row
	 * @param permission - row label
	 * @param tableInt - id assigned to that row of the table
	 * @param groupId - id assigned to the newly created user group
	 * @throws Exception
	 */
	private void checkPermissionChkBox(String permission, String tableInt, String groupId) throws Exception{
		List<WebElement> checkBoxes = browser.findElements(By.xpath("//td/input[@value='"+groupId+"_"+tableInt+"']"));
		System.out.println(permission);
		for(WebElement check:checkBoxes){
			Thread.sleep(1000);
			try {
				check.click();
			} catch (Exception e) {
				report.infoStep("Could not click on permissions checkbox:   "+e.getMessage());
				Assert.fail(e.toString());
			}
		}
		Thread.sleep(1000);
		clickElementBy("xpath", "//td/div/a[@data-permissionid='"+tableInt+"']");
		report.runStep(true, "Gave permission to Create, Read, Update, and Delete '"+permission+"'.");
	}
	/**
	 * @author nwisnewski
	 * Retrieves the group id of the newly created group.
	 * @return
	 */
	private String parseGroupId(){
		String getId = tempElementBy("xpath", "//div[contains(@id, 'zeta_grouppermissions_list')]").getAttribute("id");
		String[] parseId = getId.split("-");
		return parseId[1];
	}
}
