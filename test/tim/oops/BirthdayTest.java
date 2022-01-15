package tim.oops;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for the Birthday class, construction and comparisons */
class BirthdayTest
{
	@Test
	void testValidity() {
		Birthday b1 = new Birthday("Skyler", "White", null, 11, 8);
		assertEquals("", b1.getDescription());
		assertFalse(b1.getDate().hasYear());
		assertTrue(b1.isOk());	// null description is ok, missing year is ok

		Birthday b2 = new Birthday(null, "White", null, 7, 9);
		assertFalse(b2.isOk()); // first name can't be missing
		Birthday b3 = new Birthday("Wrong", "Month", null, 7, 19);
		assertFalse(b3.isOk()); // month can't be 19
		Birthday b4 = new Birthday("Leap", "Year", null, 29, 2);
		assertTrue(b4.isOk());  // 29th Feb _could_ be ok, depending on the year
		Birthday b5 = new Birthday("Missing", "Day", null, 29, 2, 2022);
		assertFalse(b5.isOk()); // 2022 doesn't have a 29th Feb
		Birthday b6 = new Birthday("Wrong", "Day", null, 30, 2);
		assertFalse(b6.isOk());  // 30th Feb is never ok
		Birthday b7 = new Birthday("Wrong", "Day", null, 0, 6);
		assertFalse(b7.isOk());  // 0 for either month or day is wrong
		b7 = new Birthday("Wrong", "Day", null, 6, 0);
		assertFalse(b7.isOk());  // 0 for either month or day is wrong
	}

	@Test
	void testCapitals() {
		Birthday b1 = new Birthday("hank", "schrader", null, 1, 1);
		assertEquals("Hank", b1.getFirstName());
		assertEquals("Schrader", b1.getLastName()); // both capitalised

		Birthday b2 = new Birthday("CONOR", "MacLeod", null, 10, 10, 1518);
		assertEquals("Conor", b2.getFirstName());
		assertEquals("MacLeod", b2.getLastName()); // don't lower case L

		Birthday b3 = new Birthday(" ", "Sánchez-Villalobos Ramírez", null, 2, 2);
		assertEquals("", b3.getFirstName()); // trimmed
		assertEquals("Sánchez-Villalobos Ramírez", b3.getLastName());
	}

	@Test
	void testComparisons() {
		Birthday b1 = new Birthday("Walter", "White", null, 7, 9);
		Birthday b2 = new Birthday("Walter", "White", "", 7, 9);
		assertFalse(b1 == b2);
		assertTrue(b1.equals(b2));
		assertTrue(b2.equals(b1));
		assertTrue(b2.matches(b1));

		Birthday b3 = new Birthday("Walter", "White", null, 7, 9, 1958);
		assertFalse(b3.equals(b2));
		assertTrue(b3.matches(b2));
		assertTrue(b1.matches(b3));
		assertTrue(b3.getDate().hasYear());

		Birthday b4 = new Birthday("Walt Junior", "White", "Walt's son", 8, 7, 1993);
		assertFalse(b4.matches(b2));
		Birthday b5 = new Birthday("Walt Jr", "White", "", 8, 7, 1993);
		assertTrue(b4.matches(b5));

		assertFalse(b4.matches(null));
	}
}
