package hong.specialEffects.ui;

import hong.specialEffects.R;
import hong.specialEffects.wigdet.MuiltGradeListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ExpandableListView;



public class MuiltGradeListActivity extends Activity {
  //author antkingwei
	private List<Map<String,Object>> parentList=new ArrayList<Map<String,Object>>();
	
	private List<List<Map<String,Object>>> childList = new ArrayList<List<Map<String,Object>>>();
	
	MuiltGradeListAdapter adapter;
	
	ExpandableListView exList;
	
	private String[] listName = new String[]{
			"�ҵĺ���","����ͬѧ","��ѧͬѧ","�ƶ�����","��վ����","��ͨ����"
	};
	private String[] childTitle= new String[]{
		"Ѿ��","������","С��","����","����Ѿ"	
	};
	private String[] childMood= new String[]{
		"��ϲ������","�Ҿ�������","��Ҳϲ������","�ϱ�һȺɵñ","ͬ¥��"	
	};
	private int[] headImage=new int[]{
		R.drawable.lnxins,R.drawable.lnxins,R.drawable.lnxins,R.drawable.lnxins,R.drawable.lnxins	
	};
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.muilt_grade_list_main);

        exList = (ExpandableListView) this.findViewById(R.id.expandableListView1);
        parentList =getParentList();
        childList = getChildList();
        adapter = new MuiltGradeListAdapter(MuiltGradeListActivity.this, parentList, childList);
      
        exList.setAdapter(adapter);
        exList.setGroupIndicator(null);
        exList.setDivider(null);
        
    }
    public List<Map<String,Object>> getParentList(){
    	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    	for(int i=0;i<listName.length;i++){
    		 Map<String, Object> curGroupMap = new HashMap<String, Object>();
             list.add(curGroupMap);
             curGroupMap.put("List", listName[i]);
    	}
    	return list;
    }
    public List<List<Map<String,Object>>> getChildList(){
    	List<List<Map<String,Object>>> list1 = new ArrayList<List<Map<String,Object>>>();
    	 for (int i = 0; i < listName.length; i++) {
             List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
             for (int j = 0; j <childTitle.length; j++) {
                 Map<String, Object> curChildMap = new HashMap<String, Object>();
                 children.add(curChildMap);
                 curChildMap.put("Title", childTitle[j]);
                 curChildMap.put("Mood", childMood[j]);
                 curChildMap.put("Head", headImage[j]);
             }
             list1.add(children);
    	}
    	return list1;
    	
    }
   
}
