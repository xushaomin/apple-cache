package com.appleframework.cache.core.sequence;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;

import com.appleframework.cache.core.utils.ConvertUtils;

/**
 * 机器码生成的通用服务
 * 
 * @author cruise.xu
 */
public abstract class AbstractSequenceService implements SequenceService {

	private static Logger LOG = Logger.getLogger(AbstractSequenceService.class);

	/**
	 * 对一段String生成MD5摘要信息
	 * 
	 * @param message
	 *            要摘要的String
	 * @return 生成的MD5摘要信息
	 */
	protected String getMD5(String message) {
		message += "{apdplat}";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			LOG.debug("MD5摘要长度：" + md.getDigestLength());
			byte[] b = md.digest(message.getBytes("utf-8"));
			String md5 = ConvertUtils.byte2HexString(b) + message.length();
			return getSplitString(md5);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			LOG.error("MD5摘要失败", e);
		}
		return null;
	}

	/**
	 * 将很长的字符串以固定的位数分割开，以便于人类阅读
	 * 
	 * @param str
	 * @return
	 */
	protected String getSplitString(String str) {
		return getSplitString(str, "-", 4);
	}

	/**
	 * 将很长的字符串以固定的位数分割开，以便于人类阅读 如将 71F5DA7F495E7F706D47F3E63DC6349A
	 * 以-，每4个一组，则分割为 71F5-DA7F-495E-7F70-6D47-F3E6-3DC6-349A
	 * 
	 * @param str
	 *            字符串
	 * @param split
	 *            分隔符
	 * @param length
	 *            长度
	 * @return
	 */
	protected String getSplitString(String str, String split, int length) {
		int len = str.length();
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < len; i++) {
			if (i % length == 0 && i > 0) {
				temp.append(split);
			}
			temp.append(str.charAt(i));
		}
		String[] attrs = temp.toString().split(split);
		StringBuilder finalMachineCode = new StringBuilder();
		for (String attr : attrs) {
			if (attr.length() == length) {
				finalMachineCode.append(attr).append(split);
			}
		}
		String result = finalMachineCode.toString().substring(0, finalMachineCode.toString().length() - 1);
		return result;
	}

	/**
	 * 利用sigar来生成机器码，当然这个实现不是很好，无法获得CPU ID，希望有兴趣的朋友来改进这个实现
	 * 
	 * @param osName
	 *            操作系统类型
	 * @return 机器码
	 */
	protected String getSigarSequence(String osName) {
		try {
			Set<String> result = new HashSet<>();
			Sigar sigar = new Sigar();
			String[] ifaces = sigar.getNetInterfaceList();
			for (String iface : ifaces) {
				NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(iface);
				if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress()) || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0
						|| NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
					continue;
				}
				String mac = cfg.getHwaddr();
				result.add(mac);
				LOG.debug("mac: " + mac);
			}
			if (result.size() < 1) {
				return null;
			}
			Properties props = System.getProperties();
			String javaVersion = props.getProperty("java.version");
			result.add(javaVersion);
			LOG.debug("Java的运行环境版本：    " + javaVersion);
			String javaVMVersion = props.getProperty("java.vm.version");
			result.add(javaVMVersion);
			LOG.debug("Java的虚拟机实现版本：    " + props.getProperty("java.vm.version"));
			String osVersion = props.getProperty("os.version");
			result.add(osVersion);
			LOG.debug("操作系统的版本：    " + props.getProperty("os.version"));

			Mem mem = sigar.getMem();
			// 内存总量
			String totalMem = mem.getTotal() / 1024L + "K av";
			LOG.debug("内存总量:    " + totalMem);
			result.add(totalMem);

			LOG.debug("result:    " + result);
			String machineCode = getMD5(result.toString());

			return machineCode;
		} catch (Throwable ex) {
			LOG.error("生成 " + osName + " 平台下的机器码失败", ex);
		}
		return null;
	}
}
