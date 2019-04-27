package in.novopay.platform_ui.pages.web;

import java.awt.AWTException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import in.novopay.platform_ui.utils.BasePage;
import in.novopay.platform_ui.utils.DBUtils;
import in.novopay.platform_ui.utils.Log;
import in.novopay.platform_ui.utils.MongoDBUtils;

public class ElectricityPage extends BasePage {
	DBUtils dbUtils = new DBUtils();
	MongoDBUtils mongoDbUtils = new MongoDBUtils();
	DecimalFormat df = new DecimalFormat("#.00");

	WebDriverWait wait = new WebDriverWait(wdriver, 30);
	WebDriverWait waitWelcome = new WebDriverWait(wdriver, 3);

	@FindBy(xpath = "//*[@class='fa fa-bars fa-lg text-white']")
	WebElement menu;

	@FindBy(xpath = "//*[@class='slimScrollBar']")
	WebElement scrollBar;

	@FindBy(xpath = "//span[contains(text(),'Bill Payments')]")
	WebElement billPayments;

	@FindBy(xpath = "//i[contains(@class,'np np-refresh')]")
	WebElement refreshButton;

	@FindBy(xpath = "//i[contains(@class,'np np-sync')]")
	WebElement syncButton;

	@FindBy(xpath = "//span[contains(text(),'wallet balance')]")
	WebElement retailerWallet;

	@FindBy(xpath = "//span[contains(text(),'wallet balance')]/parent::p/following-sibling::p/span")
	WebElement retailerWalletBalance;

	@FindBy(xpath = "//span[contains(text(),'cashout balance')]")
	WebElement cashoutWallet;

	@FindBy(xpath = "//span[contains(text(),'cashout balance')]/parent::p/following-sibling::p/span")
	WebElement cashoutWalletBalance;

	@FindBy(xpath = "//span[contains(text(),'merchant balance')]")
	WebElement merchantWallet;

	@FindBy(xpath = "//span[contains(text(),'merchant balance')]/parent::p/following-sibling::p/span")
	WebElement merchantWalletBalance;

	@FindBy(xpath = "//h1[contains(text(),'Billers')]")
	WebElement pageTitle;

	@FindBy(xpath = "//span[contains(text(),'ELECTRICITY')]/parent::li")
	WebElement electricityIcon;

	@FindBy(id = "money-transfer-mobile-number")
	WebElement payerMobNum;

	@FindBy(id = "money-transfer-customer-name")
	WebElement payerName;

	@FindBy(xpath = "//div[contains(text(),'Pay New Bill')]/parent::div/parent::div")
	WebElement payNewBillButton;
	//*[@class='biller-cards']
	@FindBy(className = "biller-cards")
	WebElement billerCards;
	
	@FindBy(xpath = "//*[@id='ifsc-search-state']//span[contains(text(),'Select...')]/parent::span")
	WebElement billerList;

	@FindBy(xpath = "(//input[@id='money-transfer-beneficiary-name'])[1]")
	WebElement id1;

	@FindBy(xpath = "(//input[@id='money-transfer-beneficiary-name'])[2]")
	WebElement id2;

	@FindBy(xpath = "(//input[@id='money-transfer-beneficiary-name'])[3]")
	WebElement id3;

	@FindBy(xpath = "//div[contains(text(),'Biller Name')]/following-sibling::div[contains(@class,'bill-value')]")
	WebElement fetchedBillerName;

	@FindBy(xpath = "//button[contains(text(),'Proceed')]")
	WebElement proceedButton;

	@FindBy(xpath = "//button[contains(text(),'Clear')]")
	WebElement clearButton;

	@FindBy(xpath = "//button[contains(text(),'Proceed to Pay')]")
	WebElement proceedToPayButton;

	@FindBy(xpath = "//h5[contains(text(),'Enter 4 digit PIN')]")
	WebElement MPINScreen;

	@FindBy(id = "money-transfer-mpin-number")
	WebElement enterMPIN;

	@FindBy(xpath = "//h5[contains(text(),'Enter 4 digit PIN')]/parent::div/following-sibling::div/following-sibling::div/button[contains(text(),'Submit')]")
	WebElement submitMPIN;

	@FindBy(xpath = "//div//button[contains(text(),'Cancel')]")
	WebElement cancelButton;

	@FindBy(xpath = "//pin-modal/div//button[contains(text(),'Submit')]")
	WebElement mpinSubmitButton;

	@FindBy(xpath = "//h4[contains(text(),'Processing...')]")
	WebElement processingScreen;

	@FindBy(xpath = "//div[contains(@class,'recharge-modal')]/div/div/div/h4[contains(text(),'!')]")
	WebElement elecTxnScreen;

	@FindBy(xpath = "//div[contains(@class,'recharge-modal')]/div/div/div")
	WebElement elecTxnScreenType;

	@FindBy(xpath = "//div[contains(@class,'recharge-modal')]/div/div/div/following-sibling::div/div[1]")
	WebElement elecTxnScreenMessage;

	@FindBy(xpath = "//button[contains(text(),'Exit')]")
	WebElement exitButton;

	@FindBy(xpath = "//button[contains(text(),'Retry')]")
	WebElement retryButton;

	@FindBy(xpath = "//button[contains(text(),'Done')]")
	WebElement doneButton;

	@FindBy(xpath = "//div[contains(@class,'toast-message')]")
	WebElement toasterMsg;

	// Load all objects
	public ElectricityPage(WebDriver wdriver) {
		super(wdriver);
		PageFactory.initElements(wdriver, this);
	}

	// Perform action on page based on given commands
	public void electricity(Map<String, String> usrData)
			throws InterruptedException, AWTException, IOException, ClassNotFoundException {

		try {
			// Update retailer wallet balance to 0 for scenario where amount > wallet
			if (usrData.get("ASSERTION").equalsIgnoreCase("Insufficient Balance")) {
				dbUtils.updateWalletBalance(mobileNumFromIni(), "retailer", "0");
			}

			menu.click();
			refreshBalance(); // refresh wallet balances
			menu.click();
			menu.click();
			wait.until(ExpectedConditions.elementToBeClickable(scrollBar));
			scrollElementDown(scrollBar, billPayments);
			Log.info("Bill Payments clicked");
			wait.until(ExpectedConditions.elementToBeClickable(pageTitle));
			menu.click();

			displayInitialBalance(usrData, "retailer"); // display wallet balances in console

			double initialWalletBalance = getInitialBalance("retailer"); // store wallet balance as double datatype

			// Click on payer mobile number field
			wait.until(ExpectedConditions.elementToBeClickable(payerMobNum));
			clickElement(payerMobNum);
			payerMobNum.clear();
			payerMobNum.sendKeys(getCustomerDetailsFromIni(usrData.get("PAYERMOBILENUMBER")));
			Log.info("Payer mobile number entered");

			if (usrData.get("PAYERNAME").equalsIgnoreCase("NewName")) {
				// Click on payer name number field
				wait.until(ExpectedConditions.elementToBeClickable(payerName));
				clickElement(payerName);
				payerName.clear();
				payerName.sendKeys(getCustomerDetailsFromIni("NewName"));
				Log.info("Payer name entered");
			} else {
				Assert.assertEquals(payerName.getText(), getCustomerDetailsFromIni("ExistingName"));
				Log.info("Payer name is " + payerName.getText());
			}

			// Click on electricity icon
			wait.until(ExpectedConditions.elementToBeClickable(electricityIcon));
			clickElement(electricityIcon);
			Log.info("Electricity icon clicked");

			waitForSpinner();

			if (usrData.get("PAYERMOBILENUMBER").equalsIgnoreCase("ExistingNum")) {
				wait.until(ExpectedConditions.elementToBeClickable(billerCards));
				clickElement(billerCards);
				Log.info("Biller Card is clicked");
			}
			// Click on pay new bill button
			wait.until(ExpectedConditions.elementToBeClickable(payNewBillButton));
			clickElement(payNewBillButton);
			Log.info("Pay New Bill button clicked");

			wait.until(ExpectedConditions.elementToBeClickable(billerList));
			billerList.click();
			Log.info("Biller drop down clicked");
			String ifscState = "//li[contains(text(),'" + usrData.get("BILLERNAME") + "')]";
			WebElement ifscSearchState = wdriver.findElement(By.xpath(ifscState));
			ifscSearchState.click();
			Log.info(usrData.get("BILLERNAME") + " selected");

			if (usrData.get("BILLERNAME").equalsIgnoreCase("Bangalore Electricity Supply Company")) {
				wait.until(ExpectedConditions.elementToBeClickable(id1));
				clickElement(id1);
				id1.sendKeys(usrData.get("ACCOUNTID"));
				Log.info("Account Id entered");
			} else if (usrData.get("BILLERNAME").equalsIgnoreCase("MSEDC Limited")) {
				wait.until(ExpectedConditions.elementToBeClickable(id1));
				clickElement(id1);
				id1.sendKeys(usrData.get("ACCOUNTID"));
				Log.info("Account Id entered");
				wait.until(ExpectedConditions.elementToBeClickable(id2));
				clickElement(id2);
				id2.sendKeys(usrData.get("BILLINGUNIT"));
				Log.info("Billing Unit entered");
				wait.until(ExpectedConditions.elementToBeClickable(id3));
				clickElement(id3);
				id3.sendKeys(usrData.get("PROSCYCLE"));
				Log.info("Processing cycle entered");
			}

			if (usrData.get("PROCEED").equalsIgnoreCase("YES")) {
				// Click on Proceed button
				wait.until(ExpectedConditions.elementToBeClickable(proceedButton));
				clickElement(proceedButton);

				waitForSpinner();

			} else if (usrData.get("PROCEED").equalsIgnoreCase("Clear")) {
				// Click on Clear button
				clickElement(clearButton);
			}

			if (usrData.get("PROCEEDTOPAY").equalsIgnoreCase("Yes")) {
				// Click on Proceed to pay button
				wait.until(ExpectedConditions.visibilityOf(proceedToPayButton));
				clickElement(proceedToPayButton);

				wait.until(ExpectedConditions.visibilityOf(MPINScreen));
				Log.info("MPIN screen displayed");
				wait.until(ExpectedConditions.elementToBeClickable(enterMPIN));
				enterMPIN.click();
				if (usrData.get("MPIN").equalsIgnoreCase("Valid")) {
					enterMPIN.sendKeys(getAuthfromIni("MPIN"));
				} else if (usrData.get("MPIN").equalsIgnoreCase("Invalid")) {
					enterMPIN.sendKeys("9999");
				}
				Log.info("MPIN entered");

				String mpinButtonName = usrData.get("MPINSCREENBUTTON");
				String mpinScreenButtonXpath = "//h5[contains(text(),'Enter 4 digit PIN')]/parent::div/"
						+ "following-sibling::div/following-sibling::div/button[contains(text(),'" + mpinButtonName
						+ "')]";
				WebElement mpinScreenButton = wdriver.findElement(By.xpath(mpinScreenButtonXpath));
				wait.until(ExpectedConditions.elementToBeClickable(mpinScreenButton));
				mpinScreenButton.click();
				Log.info(mpinButtonName + " button clicked");
				if (mpinButtonName.equalsIgnoreCase("Cancel")) {
					Log.info("Cancel button clicked");
				} else if (mpinButtonName.equalsIgnoreCase("Submit")) {
					waitForSpinner();

					wait.until(ExpectedConditions.visibilityOf(elecTxnScreen));
					Log.info("Txn screen displayed");

					// Update retailer wallet balance to 1000000 for scenario where amount > wallet
					if (usrData.get("ASSERTION").equalsIgnoreCase("Insufficient Balance")) {
						dbUtils.updateWalletBalance(mobileNumFromIni(), "retailer", "1000000");
					}

					// Verify the details on transaction screen
					if (elecTxnScreen.getText().equalsIgnoreCase("Success!")) {
//						assertionOnSuccessScreen(usrData);
//						assertionOnSMS(usrData);

						wait.until(ExpectedConditions.elementToBeClickable(doneButton));
						doneButton.click();
						Log.info("Done button clicked");
						refreshBalance();
//						verifyUpdatedBalanceAfterSuccessTxn(usrData, initialWalletBalance);
					} else if (elecTxnScreen.getText().equalsIgnoreCase("Failed!")) {
						if (usrData.get("MPIN").equalsIgnoreCase("Valid")) {
//							assertionOnFailedScreen(usrData);
//							assertionOnSMS(usrData);
							if (usrData.get("TXNSCREENBUTTON").equalsIgnoreCase("Exit")) {
								Log.info("Clicking exit button");
							} else if (usrData.get("TXNSCREENBUTTON").equalsIgnoreCase("Retry")) {
								retryButton.click();
								wait.until(ExpectedConditions.visibilityOf(MPINScreen));
								Log.info("MPIN screen displayed");
								wait.until(ExpectedConditions.elementToBeClickable(enterMPIN));
								enterMPIN.click();
								enterMPIN.sendKeys(getAuthfromIni("MPIN"));
								Log.info("MPIN entered");
								wait.until(ExpectedConditions.elementToBeClickable(submitMPIN));
								submitMPIN.click();
								Log.info("Submit button clicked");
								wait.until(ExpectedConditions.visibilityOf(processingScreen));
								Log.info("Processing screen displayed");
								wait.until(ExpectedConditions.visibilityOf(elecTxnScreen));
								Log.info("Txn screen displayed");
								assertionOnFailedScreen(usrData);
							}
							wait.until(ExpectedConditions.elementToBeClickable(exitButton));
							exitButton.click();
							Log.info("Exit button clicked");
						} else if (usrData.get("MPIN").equalsIgnoreCase("Invalid")) {
							wait.until(ExpectedConditions.elementToBeClickable(elecTxnScreenMessage));
							Log.info(elecTxnScreenMessage.getText());
							if (usrData.get("TXNSCREENBUTTON").equalsIgnoreCase("Exit")) {
								exitButton.click();
								Log.info("Exit button clicked");
							} else if (usrData.get("TXNSCREENBUTTON").equalsIgnoreCase("Retry")) {
								retryButton.click();
								wait.until(ExpectedConditions.visibilityOf(MPINScreen));
								Log.info("MPIN screen displayed");
								wait.until(ExpectedConditions.elementToBeClickable(enterMPIN));
								enterMPIN.click();
								enterMPIN.sendKeys(getAuthfromIni("MPIN"));
								Log.info("MPIN entered");
								wait.until(ExpectedConditions.elementToBeClickable(submitMPIN));
								submitMPIN.click();
								Log.info("Submit button clicked");
								wait.until(ExpectedConditions.visibilityOf(processingScreen));
								Log.info("Processing screen displayed");
								wait.until(ExpectedConditions.visibilityOf(elecTxnScreen));
								Log.info("Txn screen displayed");
								assertionOnSuccessScreen(usrData);
								doneButton.click();
								Log.info("Done button clicked");
								refreshBalance();
							}
						}
					}
				}
			} else if (usrData.get("PROCEEDTOPAY").equalsIgnoreCase("No")) {
				wait.until(ExpectedConditions.elementToBeClickable(fetchedBillerName));
				Assert.assertEquals(fetchedBillerName.getText(), usrData.get("BILLERNAME"));
				Log.info("Biller name is" + usrData.get("BILLERNAME"));
			} else if (usrData.get("PROCEEDTOPAY").equalsIgnoreCase("Clear")) {
				clearButton.click();
				Log.info("Clear button clicked");
			}
		} catch (Exception e) {
			wdriver.navigate().refresh();
			e.printStackTrace();
			Log.info("Test Case Failed");
			Assert.fail();
		}
	}

	// Show balances in console
	public void displayInitialBalance(Map<String, String> usrData, String wallet) throws ClassNotFoundException {
		String walletBalance = dbUtils.getWalletBalance(mobileNumFromIni(), "retailer");
		String walletBal = walletBalance.substring(0, walletBalance.length() - 4);
		String cashoutBalance = dbUtils.getWalletBalance(mobileNumFromIni(), "cashout");
		String cashoutBal = cashoutBalance.substring(0, cashoutBalance.length() - 4);
		String merchantBalance = dbUtils.getWalletBalance(mobileNumFromIni(), "merchant");
		String merchantBal = merchantBalance.substring(0, merchantBalance.length() - 4);

		String initialWalletBal = replaceSymbols(retailerWalletBalance.getText());
		String initialCashoutBal = replaceSymbols(cashoutWalletBalance.getText());
		String initialMerchantBal = replaceSymbols(merchantWalletBalance.getText());

		// Compare wallet balance shown in WebApp to DB
		if (usrData.get("ASSERTION").equals("Initial Balance")) {
			Assert.assertEquals(walletBal, initialWalletBal);
			Assert.assertEquals(cashoutBal, initialCashoutBal);
			Assert.assertEquals(merchantBal, initialMerchantBal);
		}

		if (wallet.equalsIgnoreCase("retailer")) {
			Log.info("Retailer Balance: " + initialWalletBal);
		} else if (wallet.equalsIgnoreCase("cashout")) {
			Log.info("Cashout Balance: " + initialCashoutBal);
		} else if (wallet.equalsIgnoreCase("merchant")) {
			Log.info("Merchant Balance: " + initialMerchantBal);
		}
	}

	// Get wallet(s) balance
	@SuppressWarnings("null")
	public double getInitialBalance(String wallet) throws ClassNotFoundException {
		String initialWalletBal = replaceSymbols(retailerWalletBalance.getText());
		String initialCashoutBal = replaceSymbols(cashoutWalletBalance.getText());
		String initialMerchantBal = replaceSymbols(merchantWalletBalance.getText());

		// Converting balance from String to Double and returning the same
		if (wallet.equalsIgnoreCase("retailer")) {
			return Double.parseDouble(initialWalletBal);
		} else if (wallet.equalsIgnoreCase("cashout")) {
			return Double.parseDouble(initialCashoutBal);
		} else if (wallet.equalsIgnoreCase("merchant")) {
			return Double.parseDouble(initialMerchantBal);
		}
		return (Double) null;
	}

	// To refresh the wallet balance
	public void refreshBalance() throws InterruptedException {
		wait.until(ExpectedConditions.elementToBeClickable(refreshButton));
		clickInvisibleElement(refreshButton);
		wait.until(ExpectedConditions.elementToBeClickable(syncButton));
		wait.until(ExpectedConditions.elementToBeClickable(refreshButton));
		Log.info("Balance refreshed successfully");
	}

	// Scroll down the page
	public void pageScrollDown() {
		JavascriptExecutor jse = (JavascriptExecutor) wdriver;
		jse.executeScript("scroll(0, 250);");
	}

	// Wait for screen to complete loading
	public void waitForSpinner() {
		wait.until(ExpectedConditions
				.invisibilityOfElementLocated(By.xpath("//div[contains(@class,'spinner')]/parent::div")));
		Log.info("Please wait...");
	}

	// Get Partner name
	public String partner() {
		return "RBL";
	}

	// Get mobile number from Ini file
	public String mobileNumFromIni() {
		return getLoginMobileFromIni(partner().toUpperCase() + "RetailerMobNum");
	}

	// Remove rupee symbol and comma from the string
	public String replaceSymbols(String element) {
		String editedElement = element.replaceAll("₹", "").replaceAll(",", "").trim();
		return editedElement;
	}

	// click on WebElement forcefully
	public void clickElement(WebElement element) {
		try {
			element.click();
		} catch (Exception e) {
			clickInvisibleElement(element);
		}
	}

	// Verify details on success screen
	public void assertionOnSuccessScreen(Map<String, String> usrData)
			throws ClassNotFoundException, ParseException, InterruptedException {
		Assert.assertEquals(elecTxnScreenMessage.getText(), "Deposit to Capital-First success.");
		Log.info(elecTxnScreenMessage.getText());
	}

	// Verify details on failed screen
	public void assertionOnFailedScreen(Map<String, String> usrData)
			throws ClassNotFoundException, ParseException, InterruptedException {
		if (usrData.get("ASSERTION").equalsIgnoreCase("Invalid MPIN")) {
			Assert.assertEquals(elecTxnScreenMessage.getText(), "Authentication Failed Invalid MPIN");
		} else if (usrData.get("ASSERTION").equalsIgnoreCase("Insufficient Balance")) {
			Assert.assertEquals(elecTxnScreenMessage.getText(), "Insufficient balance");
		} else {
			Assert.assertEquals(elecTxnScreenMessage.getText(),
					"Deposit to Capital First failed. Transaction reversed successfully.");
		}
		Log.info(elecTxnScreenMessage.getText());
	}

	// SMS assertion
	public void assertionOnSMS(Map<String, String> usrData) throws ClassNotFoundException, InterruptedException {
		String successSMS = "Success! Deposit of Rs " + cmsDetailsFromIni("CfAmount", "") + " for BATCH-ID "
				+ cmsDetailsFromIni("CfBatchId", "") + " was successful.";
		String failSMS = "Failure! Deposit of Rs " + cmsDetailsFromIni("CfAmount", "") + " for BATCH-ID "
				+ cmsDetailsFromIni("CfBatchId", "") + " failed.";
		Thread.sleep(5000);
		if (usrData.get("ASSERTION").equalsIgnoreCase("Success SMS")) {
			Assert.assertEquals(successSMS, dbUtils.sms());
			Log.info(successSMS);
		} else if (usrData.get("ASSERTION").equalsIgnoreCase("Fail SMS")) {
			Assert.assertEquals(failSMS, dbUtils.sms());
			Log.info(successSMS);
		}
	}

	// Assertion after success or orange screen is displayed
	public void verifyUpdatedBalanceAfterSuccessTxn(Map<String, String> usrData, double initialWalletBalance)
			throws ClassNotFoundException {
		double amount = Double.parseDouble(cmsDetailsFromIni("CfAmount", ""));
		double comm = amount * 4 / 1000;
		double commission = Math.round(comm * 100.0) / 100.0;
		double taxDS = commission * Double.parseDouble(dbUtils.getTDSPercentage(mobileNumFromIni())) / 10000;
		double tds = Math.round(taxDS * 100.0) / 100.0;
		double newWalletBal = initialWalletBalance - amount + commission - tds;
		txnDetailsFromIni("StoreComm", String.valueOf(commission));
		txnDetailsFromIni("StoreTds", String.valueOf(tds));
		String newWalletBalance = df.format(newWalletBal);
		Assert.assertEquals(replaceSymbols(retailerWalletBalance.getText()), newWalletBalance);
		Log.info("Updated Retailer Wallet Balance: " + replaceSymbols(retailerWalletBalance.getText()));
	}
}