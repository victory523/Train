import { WeightMeasurement } from "src/app/services/weight.service";

function daysBefore(days: number): Date {
  return new Date(new Date().getTime() - days * 1000 * 60 * 60 * 24);
}

export const weightMeasurements = [
  { date: daysBefore(459), weight: 108.9 },
  { date: daysBefore(457), weight: 108.3 },
  { date: daysBefore(454), weight: 107.8 },
  { date: daysBefore(453), weight: 107.5 },
  { date: daysBefore(452), weight: 108.7 },
  { date: daysBefore(451), weight: 108.6 },
  { date: daysBefore(448), weight: 107.4 },
  { date: daysBefore(447), weight: 107.8 },
  { date: daysBefore(445), weight: 107.7 },
  { date: daysBefore(444), weight: 107.2 },
  { date: daysBefore(443), weight: 107.4 },
  { date: daysBefore(442), weight: 106.5 },
  { date: daysBefore(441), weight: 106.9 },
  { date: daysBefore(440), weight: 107.1 },
  { date: daysBefore(439), weight: 107.4 },
  { date: daysBefore(438), weight: 106.8 },
  { date: daysBefore(437), weight: 106.9 },
  { date: daysBefore(436), weight: 107 },
  { date: daysBefore(435), weight: 106.4 },
  { date: daysBefore(434), weight: 106 },
  { date: daysBefore(433), weight: 105.9 },
  { date: daysBefore(432), weight: 106 },
  { date: daysBefore(431), weight: 105.6 },
  { date: daysBefore(430), weight: 105.4 },
  { date: daysBefore(429), weight: 105.1 },
  { date: daysBefore(428), weight: 105.7 },
  { date: daysBefore(427), weight: 105.4 },
  { date: daysBefore(426), weight: 105.3 },
  { date: daysBefore(425), weight: 105.8 },
  { date: daysBefore(424), weight: 105.6 },
  { date: daysBefore(423), weight: 105.2 },
  { date: daysBefore(422), weight: 104.4 },
  { date: daysBefore(421), weight: 104.5 },
  { date: daysBefore(420), weight: 104.4 },
  { date: daysBefore(419), weight: 104.4 },
  { date: daysBefore(418), weight: 105.2 },
  { date: daysBefore(417), weight: 105 },
  { date: daysBefore(416), weight: 104.4 },
  { date: daysBefore(415), weight: 104.2 },
  { date: daysBefore(414), weight: 103.8 },
  { date: daysBefore(413), weight: 103.9 },
  { date: daysBefore(412), weight: 104.4 },
  { date: daysBefore(411), weight: 104.1 },
  { date: daysBefore(410), weight: 104.8 },
  { date: daysBefore(409), weight: 105 },
  { date: daysBefore(408), weight: 104.6 },
  { date: daysBefore(407), weight: 104 },
  { date: daysBefore(406), weight: 103.8 },
  { date: daysBefore(405), weight: 103.4 },
  { date: daysBefore(404), weight: 103.2 },
  { date: daysBefore(403), weight: 104 },
  { date: daysBefore(402), weight: 103.4 },
  { date: daysBefore(401), weight: 103.3 },
  { date: daysBefore(400), weight: 103 },
  { date: daysBefore(399), weight: 103 },
  { date: daysBefore(398), weight: 103.5 },
  { date: daysBefore(397), weight: 103.7 },
  { date: daysBefore(395), weight: 102.8 },
  { date: daysBefore(394), weight: 102.6 },
  { date: daysBefore(393), weight: 102.5 },
  { date: daysBefore(392), weight: 101.5 },
  { date: daysBefore(391), weight: 102 },
  { date: daysBefore(390), weight: 102.6 },
  { date: daysBefore(389), weight: 102.7 },
  { date: daysBefore(388), weight: 102.3 },
  { date: daysBefore(387), weight: 102.2 },
  { date: daysBefore(386), weight: 102.3 },
  { date: daysBefore(385), weight: 102.1 },
  { date: daysBefore(384), weight: 101.4 },
  { date: daysBefore(383), weight: 101.6 },
  { date: daysBefore(382), weight: 101.6 },
  { date: daysBefore(381), weight: 101.8 },
  { date: daysBefore(380), weight: 101.3 },
  { date: daysBefore(377), weight: 101.8 },
  { date: daysBefore(376), weight: 101.8 },
  { date: daysBefore(375), weight: 102 },
  { date: daysBefore(374), weight: 102.3 },
  { date: daysBefore(373), weight: 101.9 },
  { date: daysBefore(372), weight: 101.7 },
  { date: daysBefore(371), weight: 101 },
  { date: daysBefore(370), weight: 101.5 },
  { date: daysBefore(369), weight: 101.2 },
  { date: daysBefore(368), weight: 101.5 },
  { date: daysBefore(367), weight: 101.5 },
  { date: daysBefore(366), weight: 101.1 },
  { date: daysBefore(365), weight: 100.5 },
  { date: daysBefore(364), weight: 100.3 },
  { date: daysBefore(363), weight: 100.6 },
  { date: daysBefore(362), weight: 100.5 },
  { date: daysBefore(361), weight: 101 },
  { date: daysBefore(360), weight: 100.9 },
  { date: daysBefore(359), weight: 99.8 },
  { date: daysBefore(358), weight: 100 },
  { date: daysBefore(357), weight: 100.8 },
  { date: daysBefore(356), weight: 100.9 },
  { date: daysBefore(355), weight: 100.4 },
  { date: daysBefore(354), weight: 100.2 },
  { date: daysBefore(353), weight: 99.7 },
  { date: daysBefore(352), weight: 99.3 },
  { date: daysBefore(351), weight: 99.2 },
  { date: daysBefore(350), weight: 98.9 },
  { date: daysBefore(349), weight: 99.6 },
  { date: daysBefore(348), weight: 99.6 },
  { date: daysBefore(347), weight: 99.7 },
  { date: daysBefore(346), weight: 99.3 },
  { date: daysBefore(345), weight: 98.8 },
  { date: daysBefore(344), weight: 99.4 },
  { date: daysBefore(343), weight: 99.2 },
  { date: daysBefore(342), weight: 98.9 },
  { date: daysBefore(341), weight: 99.1 },
  { date: daysBefore(340), weight: 98.4 },
  { date: daysBefore(339), weight: 98.3 },
  { date: daysBefore(338), weight: 98.5 },
  { date: daysBefore(337), weight: 98.4 },
  { date: daysBefore(336), weight: 98.9 },
  { date: daysBefore(335), weight: 98.5 },
  { date: daysBefore(334), weight: 99.1 },
  { date: daysBefore(333), weight: 98.9 },
  { date: daysBefore(332), weight: 98.3 },
  { date: daysBefore(331), weight: 98 },
  { date: daysBefore(330), weight: 97.9 },
  { date: daysBefore(329), weight: 97.8 },
  { date: daysBefore(328), weight: 98.1 },
  { date: daysBefore(327), weight: 98.5 },
  { date: daysBefore(326), weight: 98 },
  { date: daysBefore(325), weight: 97.8 },
  { date: daysBefore(324), weight: 97.9 },
  { date: daysBefore(323), weight: 97.9 },
  { date: daysBefore(322), weight: 97.4 },
  { date: daysBefore(321), weight: 97.8 },
  { date: daysBefore(320), weight: 98.1 },
  { date: daysBefore(319), weight: 97.7 },
  { date: daysBefore(318), weight: 97.4 },
  { date: daysBefore(317), weight: 96.7 },
  { date: daysBefore(316), weight: 97 },
  { date: daysBefore(315), weight: 96.3 },
  { date: daysBefore(314), weight: 96.8 },
  { date: daysBefore(313), weight: 97.1 },
  { date: daysBefore(312), weight: 96.9 },
  { date: daysBefore(311), weight: 97.8 },
  { date: daysBefore(310), weight: 98.5 },
  { date: daysBefore(309), weight: 97.8 },
  { date: daysBefore(308), weight: 98.6 },
  { date: daysBefore(307), weight: 97.8 },
  { date: daysBefore(306), weight: 97.3 },
  { date: daysBefore(305), weight: 97.8 },
  { date: daysBefore(304), weight: 98 },
  { date: daysBefore(303), weight: 97 },
  { date: daysBefore(302), weight: 96.7 },
  { date: daysBefore(301), weight: 96.7 },
  { date: daysBefore(300), weight: 97 },
  { date: daysBefore(299), weight: 98.2 },
  { date: daysBefore(298), weight: 96.3 },
  { date: daysBefore(297), weight: 96 },
  { date: daysBefore(296), weight: 95.9 },
  { date: daysBefore(295), weight: 95.4 },
  { date: daysBefore(294), weight: 95.1 },
  { date: daysBefore(293), weight: 95.3 },
  { date: daysBefore(292), weight: 95.3 },
  { date: daysBefore(291), weight: 95.2 },
  { date: daysBefore(290), weight: 95.4 },
  { date: daysBefore(289), weight: 94.8 },
  { date: daysBefore(288), weight: 95.2 },
  { date: daysBefore(287), weight: 94.5 },
  { date: daysBefore(286), weight: 94 },
  { date: daysBefore(285), weight: 94.6 },
  { date: daysBefore(284), weight: 94.8 },
  { date: daysBefore(283), weight: 95.1 },
  { date: daysBefore(282), weight: 94.7 },
  { date: daysBefore(281), weight: 94.7 },
  { date: daysBefore(280), weight: 94.8 },
  { date: daysBefore(279), weight: 94.6 },
  { date: daysBefore(278), weight: 95.1 },
  { date: daysBefore(274), weight: 93.8 },
  { date: daysBefore(271), weight: 93.7 },
  { date: daysBefore(268), weight: 93.1 },
  { date: daysBefore(262), weight: 92.8 },
  { date: daysBefore(260), weight: 92.3 },
  { date: daysBefore(247), weight: 92.1 },
  { date: daysBefore(237), weight: 92.2 },
  { date: daysBefore(233), weight: 92 },
  { date: daysBefore(232), weight: 91.8 },
  { date: daysBefore(193), weight: 91.4 },
  { date: daysBefore(190), weight: 91.1 },
  { date: daysBefore(189), weight: 90.9 },
  { date: daysBefore(179), weight: 91.9 },
  { date: daysBefore(174), weight: 90.2 },
  { date: daysBefore(168), weight: 90.1 },
  { date: daysBefore(166), weight: 90.8 },
  { date: daysBefore(162), weight: 89.9 },
  { date: daysBefore(161), weight: 89.9 },
  { date: daysBefore(160), weight: 91 },
  { date: daysBefore(159), weight: 91 },
  { date: daysBefore(158), weight: 90.3 },
  { date: daysBefore(157), weight: 89.6 },
  { date: daysBefore(156), weight: 89.5 },
  { date: daysBefore(151), weight: 89.8 },
  { date: daysBefore(149), weight: 90.5 },
  { date: daysBefore(148), weight: 89.6 },
  { date: daysBefore(147), weight: 89.9 },
  { date: daysBefore(146), weight: 89.9 },
  { date: daysBefore(145), weight: 89.7 },
  { date: daysBefore(139), weight: 89.2 },
  { date: daysBefore(133), weight: 88.6 },
  { date: daysBefore(132), weight: 89.3 },
  { date: daysBefore(118), weight: 90.4 },
  { date: daysBefore(115), weight: 90.1 },
  { date: daysBefore(110), weight: 88.8 },
  { date: daysBefore(109), weight: 89 },
  { date: daysBefore(107), weight: 88.7 },
  { date: daysBefore(99), weight: 87.5 },
  { date: daysBefore(98), weight: 88 },
  { date: daysBefore(97), weight: 88.1 },
  { date: daysBefore(96), weight: 88.1 },
  { date: daysBefore(95), weight: 87.9 },
  { date: daysBefore(94), weight: 88 },
  { date: daysBefore(93), weight: 87.8 },
  { date: daysBefore(91), weight: 87.7 },
  { date: daysBefore(89), weight: 88.9 },
  { date: daysBefore(88), weight: 88.2 },
  { date: daysBefore(87), weight: 87.7 },
  { date: daysBefore(86), weight: 87.7 },
  { date: daysBefore(85), weight: 87.5 },
  { date: daysBefore(84), weight: 87.5 },
  { date: daysBefore(82), weight: 87.9 },
  { date: daysBefore(81), weight: 87.9 },
  { date: daysBefore(80), weight: 87.6 },
  { date: daysBefore(78), weight: 86.9 },
  { date: daysBefore(76), weight: 87.5 },
  { date: daysBefore(75), weight: 87.5 },
  { date: daysBefore(74), weight: 87.4 },
  { date: daysBefore(73), weight: 87.2 },
  { date: daysBefore(72), weight: 86.5 },
  { date: daysBefore(67), weight: 86.9 },
  { date: daysBefore(66), weight: 87 },
  { date: daysBefore(65), weight: 86.9 },
  { date: daysBefore(64), weight: 86.6 },
  { date: daysBefore(63), weight: 86.6 },
  { date: daysBefore(62), weight: 86.3 },
  { date: daysBefore(61), weight: 86.5 },
  { date: daysBefore(60), weight: 86.5 },
  { date: daysBefore(59), weight: 86.2 },
  { date: daysBefore(58), weight: 86.2 },
  { date: daysBefore(57), weight: 86.2 },
  { date: daysBefore(53), weight: 86.4 },
  { date: daysBefore(52), weight: 86.2 },
  { date: daysBefore(51), weight: 86.3 },
  { date: daysBefore(49), weight: 86.1 },
  { date: daysBefore(48), weight: 86.5 },
  { date: daysBefore(47), weight: 86.5 },
  { date: daysBefore(46), weight: 86.4 },
  { date: daysBefore(45), weight: 85.9 },
  { date: daysBefore(44), weight: 86 },
  { date: daysBefore(38), weight: 86.3 },
  { date: daysBefore(37), weight: 86.5 },
  { date: daysBefore(36), weight: 86.5 },
  { date: daysBefore(35), weight: 86.2 },
  { date: daysBefore(34), weight: 86 },
  { date: daysBefore(33), weight: 86.5 },
  { date: daysBefore(32), weight: 86.2 },
  { date: daysBefore(31), weight: 86.3 },
  { date: daysBefore(30), weight: 85.8 },
  { date: daysBefore(29), weight: 85.6 },
  { date: daysBefore(7), weight: 88.3 },
  { date: daysBefore(6), weight: 88.4 },
  { date: daysBefore(5), weight: 88.3 },
  { date: daysBefore(4), weight: 87.7 },
  { date: daysBefore(2), weight: 87.3 },
  { date: daysBefore(0), weight: 87.2 },
] satisfies WeightMeasurement[];
