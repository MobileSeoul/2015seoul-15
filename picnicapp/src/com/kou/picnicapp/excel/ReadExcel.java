package com.kou.picnicapp.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.kou.picnicapp.model.TargetData;

public class ReadExcel {

	public static ArrayList<TargetData> read(String inputFile) {

		File inputWorkbook = new File(inputFile);
		ArrayList<TargetData> targetDataList = new ArrayList<TargetData>();
		Workbook w = null;
		try {
			w = Workbook.getWorkbook(inputWorkbook);
			Sheet sheet = w.getSheet(0); // Get the first sheet

			for (int i = 1; i < sheet.getRows(); i++) {
				TargetData t = new TargetData();
				t.setNumber(sheet.getCell(0, i).getContents());
				t.setName(sheet.getCell(1, i).getContents());
				t.setPhoneNumber(sheet.getCell(2, i).getContents());
				t.setUuid(sheet.getCell(3, i).getContents());
				t.setMajor(Integer.parseInt(sheet.getCell(4, i).getContents()));
				t.setMinor(Integer.parseInt(sheet.getCell(5, i).getContents()));
				targetDataList.add(t);
			}

		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return targetDataList;
	}
}