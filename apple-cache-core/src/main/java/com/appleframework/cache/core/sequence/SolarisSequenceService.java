package com.appleframework.cache.core.sequence;

/**
 * 在Solaris平台上生成机器码
 * 
 * @author cruise.xu
 */
public class SolarisSequenceService extends AbstractSequenceService {
	
	@Override
	public String getSequence() {
		return getSigarSequence("solaris");
	}

	public static void main(String[] args) {
		SequenceService s = new SolarisSequenceService();
		String seq = s.getSequence();
		System.out.println(seq);
	}
}
