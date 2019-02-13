package com.sanerzone.common.support.dubbo.spring;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;

import com.alibaba.dubbo.config.spring.ReferenceBean;

@SuppressWarnings("serial")
public class DubboReferenceBean<T> extends ReferenceBean<T> {

	public DubboReferenceBean() {
		super();
	}

	public DubboReferenceBean(DubboReference reference) {
		appendAnnotation(DubboReference.class, reference);
	}
}
