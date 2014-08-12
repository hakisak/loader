import shutil
import java.lang.System

print "Setup exec_test using exec_test_setup.py"

#System Properties
user_home = java.lang.System.getProperty("user.home")
base_dir = java.lang.System.getProperty("basedir")
project_version = java.lang.System.getProperty("project.version")
artifactId = java.lang.System.getProperty("artifactId")

print("use_home=" + user_home)
print("base_dir=" + base_dir)
print("project_version=" + project_version)
print("artifactId=" + artifactId)

shutil.rmtree(base_dir + "/target/exec_test", ignore_errors=True)
shutil.copytree(base_dir + "/resources/exec_test", base_dir + "/target/exec_test")
shutil.copytree(base_dir + "/resources/" + artifactId, base_dir + "/target/exec_test/" + artifactId)

boot_jar_src = user_home +"/.m2/repository/org/xito/bootstrap/" + project_version + "/bootstrap-" + project_version + ".jar"
print "boot jar=" + boot_jar_src
shutil.copyfile(boot_jar_src, base_dir + "/target/exec_test/boot.jar")

test_jar = artifactId + "-" + project_version + "-tests.jar"
shutil.copyfile(base_dir + "/target/" + test_jar, base_dir + "/target/exec_test/" + test_jar)
