package tim.oops;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DateTest
{
	@Test
	void testDaysUntil()
	{
		Date d1 = new Date(20, 10, 1234);
		Date d2 = new Date(28, 10, 1234);
		assertEquals(8, d1.getDaysUntil(d2));
		assertEquals(-8, d2.getDaysUntil(d1));

		d1 = new Date(31, 1);
		d2 = new Date(2, 2, 2009);
		assertEquals(2, d1.getDaysUntil(d2));
		assertEquals(-2, d2.getDaysUntil(d1));

		// 2022 is not a leap year
		d1 = new Date(25, 2, 2022);
		d2 = new Date(1, 3);
		assertEquals(4, d1.getDaysUntil(d2));

		// But 2024 is
		d1 = new Date(25, 2, 2024);
		assertEquals(5, d1.getDaysUntil(d2));
	}
}
