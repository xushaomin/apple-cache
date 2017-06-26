package com.appleframework.cache.core.sequence;

/**
 * 在Linux平台上生成机器码
 * 
 * @author 杨尚川
 */
public class LinuxSequenceService extends AbstractSequenceService {
	
	@Override
	public String getSequence() {
		return getSigarSequence("linux");
	}

	public static void main(String[] args) {
		SequenceService s = new LinuxSequenceService();
		String seq = s.getSequence();
		System.out.println(seq);
	}
}