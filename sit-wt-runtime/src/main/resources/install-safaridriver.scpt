on run argv
	ignoring application responses
		tell application "Safari"
			open (item 1 of argv)
		end tell
	end ignoring
	tell application "System Events"
		tell process "Safari"
			set frontmost to true
		end tell
	end tell
	-- wait for install dialog
	delay 5
end run
