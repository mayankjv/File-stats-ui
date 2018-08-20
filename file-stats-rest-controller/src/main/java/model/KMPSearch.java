package model;

import java.util.ArrayList;

class KMPSearch{
    public int[] kmp(char[] text, char[] pattern){
        int[] temp=calculateTemporaryArray(pattern);
        int i=0,j=0;
        ArrayList<Integer> res= new ArrayList<Integer>();
        while(i<text.length&&j<pattern.length){
            if(text[i]==pattern[j]){
                i++;
                j++;
                if(j==pattern.length){
                    res.add(i-pattern.length);
                    j=0;
                }
            }
            else{
                if(j!=0){
                    j=temp[j-1];
                }
                else
                    i++;
            }
        }
        int ret[]= new int[res.size()];
        i=0;
        for(Integer k:res)
            ret[i++]=k;
        return ret;
    }
    private int[] calculateTemporaryArray(char[] pattern){
        int size=pattern.length;
        int[] res= new int[size];
        res[0]=0;
        int index=0;
        for(int i=1;i<size;i++){
            if(pattern[i]==pattern[index]){
                res[i]=index+1;
                index++;
            }
            else{
                if(index!=0){
                    index=res[index-1];
                }
                else
                    res[i]=index+1;
            }
        }
        return res;
    }
}