package com.sojson.menu.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.sojson.common.dao.user.UMenuMapper;
import com.sojson.common.dao.user.URoleMapper;
import com.sojson.common.model.UMenu;
import com.sojson.common.utils.BeanUtils;
import com.sojson.common.utils.Constants;
import com.sojson.core.mybatis.BaseMybatisDao;
import com.sojson.menu.bo.UMenuBo;
import com.sojson.menu.service.MenuService;

public class MenuServiceImpl extends BaseMybatisDao<URoleMapper> implements MenuService {

	@Autowired
	private UMenuMapper menuMapper;
	
	@Override
	public int deleteByPrimaryKey(Long id) {
		return menuMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int insert(UMenu record) {
		return menuMapper.insert(record);
	}

	@Override
	public int insertSelective(UMenu record) {
		return menuMapper.insertSelective(record);
	}

	@Override
	public UMenuBo selectByPrimaryKey(Long id) {
		UMenuBo bo = new UMenuBo();
		UMenu m = menuMapper.selectByPrimaryKey(id);
		BeanUtils.copyNotNullProperties(bo, m);
		return bo;
	}

	@Override
	public int updateByPrimaryKeySelective(UMenu record) {
		return menuMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(UMenu record) {
		return menuMapper.updateByPrimaryKey(record);
	}

	@Override
	public Map<Long, UMenuBo> findMenuAll() {
		Set<UMenu> menuSet = menuMapper.findMenuByParentId(null);
		Map<Long, UMenuBo> menuMap = new HashMap<Long, UMenuBo>();
		if(!menuSet.isEmpty()){
			for(UMenu menu : menuSet){
				UMenuBo menuBo = new UMenuBo();
				BeanUtils.copyNotNullProperties(menuBo, menu);
				menuMap.put(menuBo.getId(), menuBo);
			}
			for(UMenu m : menuSet){
				do{
					UMenu pMenu = menuMapper.selectByPrimaryKey(m.getPid());
					if(pMenu == null){//父节点为空，则设置为顶级目录
						menuMap.get(m.getId()).setParentMenuBo(menuMap.get(Constants.TREE_ROOT_ID));
					}else{
						if(menuMap.containsKey(pMenu.getId())){
							UMenuBo parentMenu = new UMenuBo();
							BeanUtils.copyNotNullProperties(parentMenu, pMenu);
							menuMap.put(pMenu.getId(), parentMenu);
						}
						menuMap.get(pMenu.getId()).setParentMenuBo(menuMap.get(pMenu.getId()));
					}
					m = menuMapper.selectByPrimaryKey(pMenu.getPid());
				}while(m != null);
			}
			
			for(UMenuBo bo: menuMap.values()){//遍历计算孩子信息
				if(bo.getParentMenuBo() != null){
					bo.getParentMenuBo().getChildrenList().add(bo);
				}
			}
		}
		return menuMap;
	}

	@Override
	public Set<UMenuBo> findMenuByParentId(Long parentId) {
		return null;
	}

	@Override
	public List<Long> findMenuIdByRole(String roleId) {
		return menuMapper.findMenuIdByRole(roleId);
	}

}