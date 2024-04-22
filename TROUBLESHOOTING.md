If you have problems building any of the projects in these repos, try some combination of the following:

# Focus on Errors First
1. Most of these projects will generate warnings and infos. Address errors first. Nothing should ever make it into the master branch with compile errors,
so you shouldn't worry about anything but the errors when building. Sometimes errors occur because of the build order. Make sure Project -> Build Automatically is checked and then try Project-> Clean -> Clean All 
Projects. This will rebuild everything. Sometimes this will clean up residual errors.
1. Keep dependencies, particularly dependencies between Git repos,  in mind - focus on the errors that have no dependencies - because they could be causing all the other errors. There are several things you can do in Eclipse to help identify where errors are occurring without looking at each of them individually:
* In the Package Explorer, look for a package with a red square with an X in it. That indicates errors in that package. See screen shot:

![Screenshot 2023-02-24 at 6 31 32 AM](https://user-images.githubusercontent.com/120406738/221204401-c1097427-4a7f-4340-acd2-dcbe7ae6a64a.png)

* By default, the Errors pane will only show 100 errors no matter how many there are. But that may not be the 100 errors you want to look at. You can change this by changing the Filters for the Errors pane. Click on the funnel at the top right corner of the Errors pane (see screen shot):

![Screenshot 2023-02-24 at 6 34 00 AM](https://user-images.githubusercontent.com/120406738/221204925-2cb84d95-289a-41f6-bc22-f1896523ed65.png)

That will open up the Filters dialog. In the lower right corner of the Filters dialog, there is a checkbox `Use limits`. Uncheck that box to turn the limits off. Or change the value in the `Items per group:` field if you want a different limit.

![Screenshot 2023-02-24 at 6 34 24 AM](https://user-images.githubusercontent.com/120406738/221205167-6482c2da-ecb9-4f35-8880-c59109a2217e.png)

* You can sort the content of the Errors pane on the values in any of the columns. Just click on the column you want to sort on. For example, sorting on the `Path` column will group all the Errors/Warnings/Infos by project. That can help identify where the errors are coming from (see screen shot):

![Screenshot 2023-02-24 at 6 41 58 AM](https://user-images.githubusercontent.com/120406738/221206668-c5c95683-1dd3-4398-80bf-4a536cefb60f.png)

# Unresolved References
1. These are typically because either there is a missing dependency in the MANIFEST.MF file *or* because the Java Build Path doesn't reference something 
it should. The MANIFEST.MF file is checked into the Git repo, so that shouldn't be the problem. So check the Java Build Path first: 
1. Select the affected project
and right click to get the context menu -> 
1. Select Properties, then ‘Java Build Path’
2. Select the 'Libraries' tab, then ‘Classpath’. The Classpath should list 'Plug-in Dependencies'.  If it does not,  and click ‘Add Library’ then 'Plug-in Dependencies' then 'Finish', 
3. If 'Plug-in Dependencies' is already listed on the Classpath, you can specify the particular missing project you need by
selecting the 'Projects' tab, then 'Classpath' then click 'Add' and select the missing project, then 'OK'.

# referencedResource the path is unmapped
See https://www.eclipse.org/forums/index.php/t/1078446/
The problem here is the classpath. Set the Java Build Path for the project that needs the referencedResource. To fix it: 

Right mouse -> Properties ‘Java Build Path’ - Select ‘Classpath’ and click ‘Add’ the select the project that generates the needed resource.

# Errors that just won't go away
1. Sometimes Eclipse leaves residue in the Problems pane. If you see errors that don't seem valid, **they may not be**. Select the errors, then right click
to display the context menu and delete them. If they are still errors, they will be regenerated when the project is rebuilt.
1. If the file looks syntactically correct, close it and reopen it. This is particularly relevant for grammar changes. XText grammar changes may 
be overlooked by edit window caches even if the runtime is restarted. Closing the file appears to flush the cache - the errors are gone when the 
file is reopened in the editor
1. You may need to update Maven and/or Gradle. These can get out of synch with Eclipse and cause builds to fail in strange ways. 
* To update Gradle, select a parent project (e.g. `com.epistimis.uddl.parent`), right click to bring up the context menu, then select Gradle -> Refresh Gradle Project. See screenshot:

![Screenshot 2023-02-23 at 2 01 01 PM](https://user-images.githubusercontent.com/120406738/221041367-5f170a36-d87b-42a7-923e-3864994a91bc.png)

* To update Maven, select any project, right click to bring up the context menu, then select Maven -> Update Project (see screenshot):

![Screenshot 2023-02-23 at 2 01 18 PM](https://user-images.githubusercontent.com/120406738/221041577-23b6b162-da2e-442e-8b3b-82f30d84b66a.png)

This will bring up a dialog box listing all the projects in your workspace. Just click the 'Select All' Button, then 'OK' (see screenshot):

![Screenshot 2023-02-23 at 2 01 45 PM](https://user-images.githubusercontent.com/120406738/221041866-d6b9f52b-a6e9-40fa-955c-215fa14d2fca.png)


3. If you still have problems, delete the .metadata directory in the eclipse workspace containing the project. Just be prepared to recreate some things afterwards. To do this properly:
* Remove all the projects from your workspace (don't delete them from disk)
* Exit Eclipse
* In a terminal / command line window, change directories to the root directory of your workspace
* Delete the `.metadata` directory in that workspace
* Reopen Eclipse and point to that workspace directory
* Add the projects back to your workspace

# Problems with Gradle
Gradle may complain about the location of your JVM. To fix this problem, you need to make sure that JAVA_HOME is set properly. You also need to set `org.gradle.java.home` in your `~/.gradle/gradle.properties`. For Mac/Linux users, see [this](https://askubuntu.com/questions/1259159/gradle-java-home-is-set-to-an-invalid-directory) 
