mkdir bin
javac -g:none -d bin -source 8 -target 8 --source-path src -verbose src/tim/oops/*.java
cp -vr src/tim/oops/images bin/tim/oops/
cd bin
jar cvfe ../oops.jar tim.oops.Forgot tim
cd ..

