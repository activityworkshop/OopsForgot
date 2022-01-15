package tim.oops;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for the BirthdayFile class, parsing and writing */
class BirthdayFileTest
{
	@Test
	void testParsingRubbish()
	{
		String[] testStrings = new String[] {null, "", "Not a valid birthday: no tabs or commas",
				"Still not valid\teven with\ttwo tabs", "Firstname\tSecondname\tParse\tFail",
				"Firstname, Secondname,,,", ",,-1,-1"
		};
		for (String shouldFail : testStrings) {
			Birthday b = BirthdayFile.parse(shouldFail);
			assertNull(b);
		}
	}

	@Test
	void testParsingWrong()
	{
		// Not completely unparseable, but containing invalid values
		String[] testStrings = new String[] {"Looks\tOK,except\t12345\t54321",
			"1\t\t\t-1\t-1", "a,b,0,0,0,0"};
		for (String shouldFail : testStrings) {
			Birthday b = BirthdayFile.parse(shouldFail);
			assertNotNull(b);
			assertFalse(b.isOk());
		}
	}

	@Test
	void testParsingGood()
	{
		String[] testStrings = new String[] {"first,second,31,1", "  first, second ,31,1,1970",
			"1\tfirst\tSecond\t31\t01\t1970\tSome description",
			"1\tfirst\tSecond\t31\t01\t1970\tSome description\tand\tsome\tother\tfields"};
		for (String shouldWork : testStrings) {
			Birthday b = BirthdayFile.parse(shouldWork);
			assertNotNull(b);
			assertTrue(b.isOk());
			assertEquals("First", b.getFirstName());
			assertEquals("Second", b.getLastName());
			assertEquals(31, b.getDate().getDayNum());
			assertEquals(1, b.getDate().getMonthNum());
			if (b.getDate().hasYear()) {
				assertEquals(1970, b.getDate().getYearNum());
			}
			if (shouldWork.contains("Some")) {
				assertEquals("Some description", b.getDescription());
			}
		}
	}
}
