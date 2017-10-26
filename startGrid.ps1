$vms = ("CAKE-WC-R1201P", "CAKE-WC-R1202P")
$macs = ("hqsml-144724", "hqsml-312384")
$hub = "CAKE-WC-R1204P"
$username = "TestingAcct_01"
$login = "Cable\${username}"
$pw = "S-atR8thU3eg"
ForEach($vm in $vms){
    pskill \\$vm -u $login -p $pw java.exe
    pskill \\$vm -u $login -p $pw iexplore.exe
    pskill \\$vm -u $login -p $pw IEDriverServer.exe
    pskill \\$vm -u $login -p $pw chrome.exe
    pskill \\$vm -u $login -p $pw chromedriver.exe
    Invoke-Command -ComputerName $vm -ScriptBlock{Import-Module -Name C:\Clear-IECachedData.psm1 
    Clear-IECachedData -All}
}
ForEach($mac in $macs){
     ssh -i C:\Users\testingacct_01\.ssh\id_${mac} testingacct_01@${mac} killall -9 java
}
Invoke-RestMethod -Uri http://${hub}:4444/lifecycle-manager?action=shutdown
Start-Process java -ArgumentList '-jar', 'selenium-server-standalone-3.6.0.jar', '-role hub', '-timeout 540' -WorkingDirectory D:\Grid
ForEach($vm in $vms){
    $output = cmd /s/c "psexec \\$vm query session"
    $sessionId = $output | Select-String "$username\s+(\w+)" | Foreach {$_.Matches[0].Groups[1].Value}
    Write-Host $sessionId -ForegroundColor Magenta
    psexec \\$vm -u $login -p $pw -i $sessionId -w D:\Grid -d java -jar selenium-server-standalone-3.6.0.jar -role webdriver -hub http://${hub}:4444/grid/register -id $vm -browser "browserName=chrome,platform=WINDOWS,maxInstances=1" -browser "ensureCleanSession=true,browserName=internet explorer,InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION=true,version=11,platform=WINDOWS,ACCEPT_SSL_CERTS=true,maxInstances=1"
}
ForEach($mac in $macs){
    $toSSH = "-i C:\Users\testingacct_01\.ssh\id_${mac} testingacct_01@${mac} /Users/testingacct_01/Documents/Grid/zetaNode.sh"
    Start-Process ssh.exe $toSSH
} 