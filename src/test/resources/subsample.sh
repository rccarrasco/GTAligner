for file in $(grep -l j samples/*.txt)
do
  cp $file samples_withj
  image=$(basename $file .txt).jpeg
  cp samples/$image samples_withj
done
