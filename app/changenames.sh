for file in `ls`
do
	filename=`echo $file|sed 's/ease_\(.*\)/\1/g'`
	echo $filename
	mv $file $filename 
done
