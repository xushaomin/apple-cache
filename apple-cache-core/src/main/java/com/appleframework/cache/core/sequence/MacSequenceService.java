package com.appleframework.cache.core.sequence;

/**
 * 在Mac OS X平台上生成机器码
 * 
 * @author cruise.xu
 */
public class MacSequenceService extends AbstractSequenceService {
	
	@Override
	public String getSequence() {
		return getSigarSequence("mac");
	}

	public static void main(String[] args) {
		SequenceService s = new MacSequenceService();
		String seq = s.getSequence();
		System.out.println(seq);
	}
}
