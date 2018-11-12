package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

/**
 * 规格组合实体类
 * 
 * @author hbbxl
 *
 */
public class Specification implements Serializable {

	private TbSpecification specification;
	private List<TbSpecificationOption> options;

	public TbSpecification getSpecification() {
		return specification;
	}

	public void setSpecification(TbSpecification specification) {
		this.specification = specification;
	}

	public List<TbSpecificationOption> getOptions() {
		return options;
	}

	public void setOptions(List<TbSpecificationOption> options) {
		this.options = options;
	}

}
